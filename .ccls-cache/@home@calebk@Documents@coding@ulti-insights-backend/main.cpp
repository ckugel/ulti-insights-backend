#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <mysql_driver.h>
#include <mysql_connection.h>
#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/prepared_statement.h>

struct StatRecord {
    int year;
    std::string tournament;
    std::string quarter;
    int year2;
    std::string tier;
    double multiplier;
    int finishPosition;
    std::string team;
    std::string player;
    std::string stat;
    double share;
    double rankValue;
};

std::vector<StatRecord> readCSV(const std::string &filename) {
    std::vector<StatRecord> records;
    std::ifstream file(filename);
    std::string line, word;

    // Skip the header
    std::getline(file, line);

    while (std::getline(file, line)) {
        std::stringstream ss(line);
        StatRecord record;

        std::getline(ss, word, ','); // Skip the first column ("")

        std::getline(ss, word, ',');
        record.year = std::stoi(word);

        std::getline(ss, word, ',');
        record.tournament = word;

        std::getline(ss, word, ',');
        record.quarter = word;

        std::getline(ss, word, ',');
        record.year2 = std::stoi(word);

        std::getline(ss, word, ',');
        record.tier = word;

        std::getline(ss, word, ',');
        record.multiplier = std::stod(word);

        std::getline(ss, word, ',');
        record.finishPosition = std::stoi(word);

        std::getline(ss, word, ',');
        record.team = word;

        std::getline(ss, word, ',');
        record.player = word;

        std::getline(ss, word, ',');
        record.stat = word;

        std::getline(ss, word, ',');
        record.share = std::stod(word);

        std::getline(ss, word, ',');
        record.rankValue = std::stod(word);

        records.push_back(record);
    }

    return records;
}

void writeToDatabase(const std::vector<StatRecord> &records) {
    sql::mysql::MySQL_Driver *driver;
    sql::Connection *con;
    sql::PreparedStatement *pstmt;

    try {
        driver = sql::mysql::get_mysql_driver_instance();
        con = driver->connect("tcp://127.0.0.1:3306", "username", "password");
        con->setSchema("database_name");

        pstmt = con->prepareStatement(
                "INSERT INTO stat_records (year, tournament, quarter, year2, tier, multiplier, finish_position, team, player, stat, share, rank_value) "
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        for (const auto &record : records) {
            pstmt->setInt(1, record.year);
            pstmt->setString(2, record.tournament);
            pstmt->setString(3, record.quarter);
            pstmt->setInt(4, record.year2);
            pstmt->setString(5, record.tier);
            pstmt->setDouble(6, record.multiplier);
            pstmt->setInt(7, record.finishPosition);
            pstmt->setString(8, record.team);
            pstmt->setString(9, record.player);
            pstmt->setString(10, record.stat);
            pstmt->setDouble(11, record.share);
            pstmt->setDouble(12, record.rankValue);
            pstmt->execute();
        }

        delete pstmt;
        delete con;
    } catch (sql::SQLException &e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }
}

int main() {
    std::string filename = "PlayerDatabase.csv";
    std::vector<StatRecord> records = readCSV(filename);
    writeToDatabase(records);
    return 0;
}
