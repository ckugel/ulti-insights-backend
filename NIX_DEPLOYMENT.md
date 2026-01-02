# 🚀 Stadisticts Nix + Cloudflare Tunnel Deployment Guide

This guide shows how to deploy Stadisticts using Nix with Cloudflare Tunnel for secure, global access without exposing ports or managing certificates.

## 🎯 Why This Setup?

- **100% Free**: No hosting costs, only domain registration (optional)
- **Secure**: No open ports, automatic HTTPS via Cloudflare
- **Reproducible**: Nix ensures identical builds everywhere
- **Global CDN**: Cloudflare's edge network for fast access worldwide
- **Simple**: H2 database eliminates external database dependencies

## 📋 Prerequisites

1. **Nix installed** with flakes enabled
2. **Cloudflare account** (free)
3. **Domain name** (optional - see free options below)

### 🆓 Free Domain Options

**Option 1: Cloudflare Tunnel Auto-Generated Domain (Easiest)**
- No setup required - Cloudflare provides a free subdomain automatically
- Format: `[tunnel-id].cfargotunnel.com`
- Example: `abc123def456.cfargotunnel.com`

**Option 2: Workers/Pages Free Subdomain**
- Create a Cloudflare Workers project
- Get a free `.workers.dev` subdomain
- Example: `stadisticts.myname.workers.dev`

**Option 3: Third-Party Free Domains**
- FreeDNS, No-IP, DuckDNS (various TLDs)
- Requires separate registration

## 🔧 Quick Start Options

### Option A: Use Auto-Generated Domain (No DNS Setup)
```bash
cd /path/to/stadisticts
nix develop

# Use default auto-generated domain
stadisticts-setup

# Deploy immediately - no DNS configuration needed!
stadisticts-deploy
```
Your API will be accessible at the auto-generated `*.cfargotunnel.com` URL shown in the output.

### Option B: Use Custom Domain
```bash
# Set your desired hostname
export HOSTNAME=stats.yourdomain.com

# Run setup
stadisticts-setup
```
Then follow DNS configuration steps below.

### Option C: Use Cloudflare Workers Subdomain
```bash
# Set Workers subdomain (create in Cloudflare dashboard first)
export HOSTNAME=stadisticts.myname.workers.dev

# Run setup
stadisticts-setup
```

This will:
- Authenticate with Cloudflare (opens browser)
- Create a tunnel named "stadisticts"
- Generate configuration files
- Show you DNS instructions

### 3. Configure DNS (in Cloudflare Dashboard)
Add a CNAME record:
- **Type**: CNAME
- **Name**: `stats` (or your subdomain)
- **Target**: `[tunnel-id].cfargotunnel.com` (shown in setup output)
- **Proxy status**: Proxied (orange cloud)

### 4. Deploy
```bash
stadisticts-deploy
```

Your API is now live at `https://stats.yourdomain.com`! 🎉

## 📖 Detailed Setup

### Environment Variables

Set these before running commands (optional):

```bash
export TUNNEL_NAME=stadisticts           # Tunnel name
export HOSTNAME=stats.yourdomain.com     # Your domain
export PORT=8080                         # Local port (default: 8080)
```

### Available Commands

In the Nix development shell:

| Command | Purpose |
|---------|---------|
| `stadisticts-setup` | One-time Cloudflare setup |
| `stadisticts-deploy` | Deploy with tunnel |
| `mvn spring-boot:run` | Local development |
| `nix build` | Build the application |

### File Structure

```
~/.cloudflared/
├── cert.pem              # Cloudflare auth certificate
├── config.yml            # Tunnel configuration
└── [tunnel-id].json      # Tunnel credentials

./data/
└── stadisticts.mv.db     # H2 database file (auto-created)

./logs/
└── stadisticts.log       # Application logs
```

## 🖥️ Server Deployment (NixOS)

For permanent server deployment on NixOS:

### 1. Add to your NixOS configuration:

```nix
# configuration.nix or flake.nix
{
  imports = [
    /path/to/stadisticts/flake.nix#nixosModules.stadisticts
  ];

  services.stadisticts = {
    enable = true;
    port = 8080;
    hostname = "stats.yourdomain.com";
    tunnelName = "stadisticts";
  };
}
```

### 2. Copy Cloudflare credentials:
```bash
sudo mkdir -p /var/lib/stadisticts/.cloudflared
sudo cp ~/.cloudflared/* /var/lib/stadisticts/.cloudflared/
sudo chown -R stadisticts:stadisticts /var/lib/stadisticts/.cloudflared
```

### 3. Rebuild and start:
```bash
sudo nixos-rebuild switch
sudo systemctl status stadisticts
sudo systemctl status stadisticts-tunnel
```

## 🔒 Security Features

### Application Security
- H2 console disabled in production
- Minimal endpoint exposure (only health + info)
- Compression enabled for better performance
- Structured logging with rotation

### Cloudflare Protection
- DDoS protection
- Web Application Firewall (WAF)
- Rate limiting
- Bot protection
- Analytics

### Nix Isolation
- Reproducible builds
- Isolated dependencies
- Minimal attack surface

## 📊 Monitoring & Maintenance

### Health Checks
```bash
# Local health check
curl http://localhost:8080/actuator/health

# Public health check
curl https://stats.yourdomain.com/actuator/health
```

### API Documentation
- **Swagger UI**: `https://stats.yourdomain.com/swagger-ui.html`
- **OpenAPI JSON**: `https://stats.yourdomain.com/v3/api-docs`

### Log Management
```bash
# View logs
tail -f logs/stadisticts.log

# Rotate logs (automatic, but manual if needed)
logrotate /etc/logrotate.d/stadisticts
```

### Database Backup
```bash
# H2 database backup
cp data/stadisticts.mv.db backup/stadisticts-$(date +%Y%m%d).mv.db
```

## 🔄 Updates & Maintenance

### Update Application
```bash
# Pull latest changes
git pull

# Rebuild and redeploy
nix develop
stadisticts-deploy
```

### Update Dependencies
```bash
# Update Nix flake
nix flake update

# Rebuild
nix build
```

## 🐛 Troubleshooting

### Common Issues

**Tunnel not connecting:**
```bash
# Check tunnel status
cloudflared tunnel list

# Test tunnel manually
cloudflared tunnel run stadisticts
```

**Application not starting:**
```bash
# Check Java/Maven
java --version
mvn --version

# Check application logs
tail -f logs/stadisticts.log
```

**DNS not resolving:**
- Verify CNAME record in Cloudflare dashboard
- Check that proxy is enabled (orange cloud)
- DNS propagation can take up to 24 hours

### Useful Commands

```bash
# Restart tunnel
pkill cloudflared
stadisticts-tunnel &

# Rebuild application
nix build --rebuild

# Reset H2 database
rm -rf data/stadisticts.mv.db
# Restart app to recreate with sample data
```

## 🌍 Free Domain Setup Details

### Getting a Cloudflare Workers Subdomain

1. **Log into Cloudflare Dashboard**
2. **Go to Workers & Pages** → **Overview**
3. **Create Application** → **Create Worker**
4. **Choose a subdomain**: `myname.workers.dev`
5. **Save** (you don't need to deploy actual Worker code)
6. **Use this subdomain**: `stadisticts.myname.workers.dev`

### Using Auto-Generated Tunnel Domain

The easiest option requires **zero DNS configuration**:

1. Run `stadisticts-setup` without setting `HOSTNAME`
2. Cloudflare automatically provides: `https://[tunnel-id].cfargotunnel.com`
3. This URL is immediately accessible worldwide
4. Perfect for testing, demos, or if you don't need a custom domain

Example output:
```
✅ Your tunnel is ready!
🌍 Auto-generated URL: https://abc123def456.cfargotunnel.com
📊 Health check: https://abc123def456.cfargotunnel.com/actuator/health
📖 API docs: https://abc123def456.cfargotunnel.com/swagger-ui.html
```

### Free Third-Party Options

- **FreeDNS**: Free .tk, .ml, .ga domains
- **No-IP**: Free dynamic DNS subdomains  
- **DuckDNS**: Free .duckdns.org subdomains
- **Afraid.org**: Various free domain options

## 📈 Scaling Considerations

### Current Setup Handles:
- **Concurrent users**: 100-500 (H2 + Spring Boot limits)
- **Data size**: Up to several GB (H2 file database)
- **Traffic**: Cloudflare CDN handles global traffic

### When to Scale:
- **Heavy writes**: Move to PostgreSQL/MySQL
- **Large datasets**: Consider database clustering
- **High traffic**: Add multiple instances behind load balancer

## 💡 Advanced Configuration

### Custom Tunnel Config
Edit `~/.cloudflared/config.yml`:
```yaml
tunnel: your-tunnel-id
credentials-file: ~/.cloudflared/your-tunnel-id.json

ingress:
  - hostname: api.yourdomain.com
    service: http://localhost:8080
  - hostname: admin.yourdomain.com
    service: http://localhost:8081
  - service: http_status:404
```

### Environment-Specific Builds
```bash
# Development with hot reload
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# Production build
nix build .#stadisticts

# Custom profile
SPRING_PROFILES_ACTIVE=custom-profile stadisticts-deploy
```

This setup gives you a professional, secure, and globally accessible API deployment that costs nothing to run and is trivial to maintain with Nix's reproducible builds.
