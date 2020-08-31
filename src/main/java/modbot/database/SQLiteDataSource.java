package modbot.database;

import modbot.commands.SetPrefixCommandInterface;
import modbot.utils.ReactionRoles;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDataSource implements DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);
    private final HikariDataSource ds;

    public SQLiteDataSource() {
        try{
            final File dbFile = new File("database.db");

            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    LOGGER.info("Created database file");
                } else {
                    LOGGER.info("Could not create database file");
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:database.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

        try (final Statement statement = getConnection().createStatement()) {
            final String defaultPrefix = SetPrefixCommandInterface.getDefaultPrefix();

            // language=SQLite
            statement.execute("CREATE TABLE IF NOT EXISTS guild_settings(" +
                    "guild_id BIGINT PRIMARY KEY," +
                    "prefix VARCHAR(255) NOT NULL DEFAULT '" + defaultPrefix + "'" +
                    ");");
            statement.execute("CREATE TABLE IF NOT EXISTS join_roles(" +
                    "id INTEGER PRIMARY KEY," +
                    "guild_id BIGINT NOT NULL," +
                    "role_id BIGINT NOT NULL" +
                    ");");
            statement.execute("CREATE TABLE IF NOT EXISTS reaction_roles(" +
                    "id INTEGER PRIMARY KEY," +
                    "guild_id BIGINT NOT NULL," +
                    "message_id BIGINT NOT NULL," +
                    "channel_id BIGINT NOT NULL," +
                    "emote_id BIGINT," +
                    "emoji_id TEXT," +
                    "role_id BIGINT NOT NULL" +
                    ");");
            statement.execute("CREATE TABLE IF NOT EXISTS banned_words(" +
                    "id INTEGER PRIMARY KEY," +
                    "guild_id BIGINT NOT NULL," +
                    "word TEXT NOT NULL" +
                    ");");

            statement.close();
            LOGGER.info("Tables initialised");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }


    @Override
    public String getPrefix(long guildId) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("prefix");
                }
            }

            try (final PreparedStatement insertStatement = getConnection()
                    // language=SQLite
                    .prepareStatement("INSERT INTO guild_settings(guild_id) VALUES(?)")) {

                insertStatement.setString(1, String.valueOf(guildId));

                insertStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return SetPrefixCommandInterface.getDefaultPrefix();
    }


    @Override
    public void setPrefix(long guildId, String newPrefix) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("UPDATE guild_settings SET prefix = ? WHERE guild_id = ?")) {

            preparedStatement.setString(1, newPrefix);
            preparedStatement.setLong(2, guildId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Long> getJoinRoles(long guildId) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("SELECT role_id FROM join_roles WHERE guild_id = ?")) {

            preparedStatement.setLong(1, guildId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Long> toReturn = new ArrayList<>();
                while (resultSet.next()) {
                    toReturn.add(resultSet.getLong("role_id"));
                }
                return toReturn;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Long> toReturn = new ArrayList<>();
        return toReturn;
    }

    @Override
    public void addJoinRole(long guildId, long roleID) {

        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("INSERT INTO join_roles(guild_id, role_id) VALUES(?, ?)")) {

            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, roleID);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeJoinRole(long guildId, long roleID) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("DELETE FROM join_roles(guild_id, role_id) VALUES(?, ?)")) {

            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, roleID);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ReactionRoles> getReactionRoles(long guildId) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("SELECT message_id, channel_id, emote_id, emoji_id, role_id FROM reaction_roles WHERE guild_id = ?")){

            preparedStatement.setLong(1, guildId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                List<ReactionRoles> toReturn = new ArrayList<>();
                while(resultSet.next()){
                    if(resultSet.getLong("emote_id") != 0L){
                        toReturn.add(new ReactionRoles(resultSet.getLong("message_id"), resultSet.getLong("channel_id"), resultSet.getLong("emote_id"), resultSet.getLong("role_id")));
                    }else{
                        toReturn.add(new ReactionRoles(resultSet.getLong("message_id"), resultSet.getLong("channel_id"), resultSet.getString("emoji_id"), resultSet.getLong("role_id")));
                    }

                }
                return toReturn;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void addReactionRole(long guildId, ReactionRoles reactRole) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("INSERT INTO reaction_roles(guild_id, message_id, channel_id, emote_id, emoji_id, role_id) VALUES(?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, reactRole.getMessageID());
            preparedStatement.setLong(3, reactRole.getChannelID());
            if(reactRole.isEmote()){
                preparedStatement.setLong(4, reactRole.getEmoteID());
                preparedStatement.setString(5, null);
            } else{
                preparedStatement.setNull(4, Types.BIGINT);
                preparedStatement.setString(5, reactRole.getEmoji());
            }
            preparedStatement.setLong(6, reactRole.getRoleID());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeReactionRole(long guildId, ReactionRoles reactRole) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("DELETE FROM reaction_roles(guild_id, message_id, channel_id, emote_id, emoji_id, role_id) VALUES(?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setLong(1, guildId);
            preparedStatement.setLong(2, reactRole.getMessageID());
            preparedStatement.setLong(3, reactRole.getChannelID());
            if(reactRole.isEmote()){
                preparedStatement.setLong(4, reactRole.getEmoteID());
                preparedStatement.setString(5, null);
            } else{
                preparedStatement.setNull(4, Types.BIGINT);
                preparedStatement.setString(5, reactRole.getEmoji());
            }
            preparedStatement.setLong(2, reactRole.getRoleID());
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getBannedWords(long guildId) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("SELECT word FROM banned_words WHERE guild_id = ?")){

            preparedStatement.setLong(1, guildId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                List<String> toReturn = new ArrayList<>();
                while(resultSet.next()){
                    toReturn.add(resultSet.getString("word"));
                }
                return toReturn;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void addBannedWord(long guildId, String newWord) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("INSERT INTO banned_words VALUES(?, ?)")) {
            preparedStatement.setLong(1, guildId);
            preparedStatement.setString(2, newWord);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeBannedWord(long guildId, String toRemove) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("DELETE FROM banned_words VALUES(?, ?)")) {
            preparedStatement.setLong(1, guildId);
            preparedStatement.setString(2, toRemove);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

