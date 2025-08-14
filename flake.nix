{
  description = "Stadisticts - Ultimate Frisbee Statistics API with Cloudflare Tunnel deployment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
        # Simple build script that works with network access
        dev-build = pkgs.writeShellScriptBin "stadisticts-build" ''
          set -e
          echo "Building Stadisticts..."

          # Build with Maven normally (with network access)
          ./mvnw clean package -DskipTests -Dspring.profiles.active=prod-h2

          echo "Build complete: target/stadisticts-1.0.jar"
        '';

        # Cloudflare tunnel runner
        tunnel-service = pkgs.writeShellScriptBin "stadisticts-tunnel" ''
          set -e

          TUNNEL_NAME="''${TUNNEL_NAME:-stadisticts}"

          echo "Starting Cloudflare Tunnel: $TUNNEL_NAME"
          ${pkgs.cloudflared}/bin/cloudflared tunnel run $TUNNEL_NAME
        '';

        # Main deployment script
        deploy-script = pkgs.writeShellScriptBin "stadisticts-deploy" ''
          set -e

          TUNNEL_NAME="''${TUNNEL_NAME:-stadisticts}"
          JAR_FILE="target/stadisticts-1.0.jar"

          echo "Deploying Stadisticts..."
          echo ""

          # Check if jar exists
          if [ ! -f "$JAR_FILE" ]; then
            echo "ERROR: JAR file not found: $JAR_FILE"
            echo "Run 'stadisticts-build' first to build the application"
            exit 1
          fi

          # Verify tunnel exists
          if ! ${pkgs.cloudflared}/bin/cloudflared tunnel list | grep -q "$TUNNEL_NAME"; then
            echo "ERROR: Tunnel '$TUNNEL_NAME' not found!"
            echo "Run 'stadisticts-setup' first to create the tunnel"
            exit 1
          fi

          # Get tunnel URL for display
          TUNNEL_ID=$(${pkgs.cloudflared}/bin/cloudflared tunnel list | grep "$TUNNEL_NAME" | awk '{print $1}')
          TUNNEL_URL="https://$TUNNEL_ID.cfargotunnel.com"

          echo "Starting application..."
          ${pkgs.jdk21}/bin/java \
            -Dspring.profiles.active=prod-h2 \
            -Dserver.port=''${PORT:-8080} \
            -jar $JAR_FILE &
          APP_PID=$!

          # Give app time to start
          echo "Waiting for application startup..."
          sleep 8

          # Check if app is running
          if ! ${pkgs.curl}/bin/curl -s http://localhost:''${PORT:-8080}/actuator/health > /dev/null; then
            echo "ERROR: Application failed to start properly"
            kill $APP_PID 2>/dev/null || true
            exit 1
          fi

          echo "Starting Cloudflare Tunnel..."
          ${tunnel-service}/bin/stadisticts-tunnel &
          TUNNEL_PID=$!

          # Cleanup handler
          cleanup() {
            echo ""
            echo "Shutting down Stadisticts..."
            kill $APP_PID $TUNNEL_PID 2>/dev/null || true
            wait
            echo "Shutdown complete"
          }
          trap cleanup EXIT INT TERM

          echo ""
          echo "Stadisticts is live!"
          echo "================================================"
          echo "Public URL:    $TUNNEL_URL"
          echo "Local URL:     http://localhost:''${PORT:-8080}"
          echo "Health Check:  $TUNNEL_URL/actuator/health"
          echo "API Docs:      $TUNNEL_URL/swagger-ui.html"
          echo "================================================"
          echo ""
          echo "API is accessible worldwide with automatic HTTPS!"
          echo "Share this URL with anyone to access API"
          echo ""
          echo "Press Ctrl+C to stop..."

          # Keep running until interrupted
          wait
        '';

        # Initial setup script
        setup-script = pkgs.writeShellScriptBin "stadisticts-setup" ''
          set -e

          TUNNEL_NAME="''${TUNNEL_NAME:-stadisticts}"
          CONFIG_DIR="''${HOME}/.cloudflared"

          echo "Setting up Stadisticts with Cloudflare Tunnel"
          echo "================================================"
          echo ""

          # Check cloudflared auth
          if [ ! -f "$CONFIG_DIR/cert.pem" ]; then
            echo "First time setup - authenticating with Cloudflare..."
            echo "This will open the browser to login"
            echo ""
            ${pkgs.cloudflared}/bin/cloudflared tunnel login
            echo ""
            echo "Authentication successful!"
          else
            echo "Already authenticated with Cloudflare"
          fi

          # Create or verify tunnel
          if ${pkgs.cloudflared}/bin/cloudflared tunnel list | grep -q "$TUNNEL_NAME"; then
            echo "Tunnel '$TUNNEL_NAME' already exists"
          else
            echo "Creating new tunnel: $TUNNEL_NAME"
            ${pkgs.cloudflared}/bin/cloudflared tunnel create $TUNNEL_NAME
            echo "Tunnel created successfully!"
          fi

          # Get tunnel details
          TUNNEL_ID=$(${pkgs.cloudflared}/bin/cloudflared tunnel list | grep "$TUNNEL_NAME" | awk '{print $1}')
          TUNNEL_URL="https://$TUNNEL_ID.cfargotunnel.com"

          # Create minimal config for auto-generated domain
          echo "Configuring tunnel..."
          mkdir -p $CONFIG_DIR
          cat > $CONFIG_DIR/config.yml << EOF
          tunnel: $TUNNEL_ID
          credentials-file: $CONFIG_DIR/$TUNNEL_ID.json

          ingress:
            - service: http://localhost:''${PORT:-8080}
          EOF

          echo ""
          echo "Setup complete!"
          echo "================================================"
          echo "tunnel URL: $TUNNEL_URL"
          echo ""
          echo "Next steps:"
          echo "  1. Run: stadisticts-build     # Build the application"
          echo "  2. Run: stadisticts-deploy    # Deploy with tunnel"
          echo "  3. Access API at: $TUNNEL_URL"
          echo ""
        '';

      in
      {
        packages = {
          default = dev-build;
          tunnel-service = tunnel-service;
          deploy-script = deploy-script;
          setup-script = setup-script;
          dev-build = dev-build;
        };

        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            # Core tools
            jdk21
            maven
            cloudflared

            # Utilities for development and debugging
            curl
            jq

            # Our deployment scripts
            setup-script
            deploy-script
            tunnel-service
            dev-build
          ];

          shellHook = ''
            echo "Stadisticts Development Environment"
            echo "================================================"
            echo ""
            echo "Quick Start:"
            echo "  1. stadisticts-setup     # One-time Cloudflare setup"
            echo "  2. stadisticts-build     # Build the application"
            echo "  3. stadisticts-deploy    # Deploy with free subdomain"
            echo ""
            echo "Development:"
            echo "  mvn spring-boot:run      # Local development server"
            echo "  stadisticts-build        # Build for deployment"
            echo ""
            echo "Configuration:"
            echo "  TUNNEL_NAME=''${TUNNEL_NAME:-stadisticts}"
            echo "  PORT=''${PORT:-8080}"
            echo ""

            export JAVA_HOME=${pkgs.jdk21}
            export PATH="$JAVA_HOME/bin:$PATH"
          '';
        };

        # NixOS service module for server deployment
        nixosModules.stadisticts = { config, lib, pkgs, ... }: {
          options.services.stadisticts = {
            enable = lib.mkEnableOption "Stadisticts Ultimate Frisbee API";

            port = lib.mkOption {
              type = lib.types.port;
              default = 8080;
              description = "Port to run Stadisticts on";
            };

            tunnelName = lib.mkOption {
              type = lib.types.str;
              default = "stadisticts";
              description = "Cloudflare Tunnel name";
            };

            jarPath = lib.mkOption {
              type = lib.types.path;
              description = "Path to the stadisticts JAR file";
            };
          };

          config = lib.mkIf config.services.stadisticts.enable {
            systemd.services.stadisticts = {
              description = "Stadisticts Ultimate Frisbee API";
              after = [ "network.target" ];
              wantedBy = [ "multi-user.target" ];

              serviceConfig = {
                ExecStart = "${pkgs.jdk21}/bin/java -Dspring.profiles.active=prod-h2 -Dserver.port=${toString config.services.stadisticts.port} -jar ${config.services.stadisticts.jarPath}";
                Restart = "always";
                User = "stadisticts";
                Group = "stadisticts";
                WorkingDirectory = "/var/lib/stadisticts";

                # Security hardening
                NoNewPrivileges = true;
                PrivateTmp = true;
                ProtectSystem = "strict";
                ProtectHome = true;
                ReadWritePaths = [ "/var/lib/stadisticts" ];

                Environment = [
                  "PORT=${toString config.services.stadisticts.port}"
                  "SPRING_PROFILES_ACTIVE=prod-h2"
                ];
              };
            };

            systemd.services.stadisticts-tunnel = {
              description = "Cloudflare Tunnel for Stadisticts";
              after = [ "network.target" "stadisticts.service" ];
              wantedBy = [ "multi-user.target" ];

              serviceConfig = {
                ExecStart = "${pkgs.cloudflared}/bin/cloudflared tunnel run ${config.services.stadisticts.tunnelName}";
                Restart = "always";
                User = "stadisticts";
                Group = "stadisticts";
                WorkingDirectory = "/var/lib/stadisticts";

                Environment = [
                  "TUNNEL_NAME=${config.services.stadisticts.tunnelName}"
                ];
              };
            };

            # Create dedicated user and group
            users.users.stadisticts = {
              isSystemUser = true;
              group = "stadisticts";
              home = "/var/lib/stadisticts";
              createHome = true;
              description = "Stadisticts service user";
            };

            users.groups.stadisticts = {};

            # Allow local access (optional)
            networking.firewall.allowedTCPPorts = [ config.services.stadisticts.port ];

            # Ensure data directory exists with correct permissions
            systemd.tmpfiles.rules = [
              "d /var/lib/stadisticts 0755 stadisticts stadisticts -"
              "d /var/lib/stadisticts/data 0755 stadisticts stadisticts -"
              "d /var/lib/stadisticts/logs 0755 stadisticts stadisticts -"
            ];
          };
        };
      });
}
