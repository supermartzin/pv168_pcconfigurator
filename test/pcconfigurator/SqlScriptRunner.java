/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author davidkaya
 */

package pcconfigurator;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SqlScriptRunner {

    public static final String DEFAULT_SCRIPT_DELIMETER = ";";
    public static final Logger LOGGER = Logger.getLogger(SqlScriptRunner.class.getName());

    private final boolean autoCommit, stopOnError, logToFile;
    private final Connection connection;

    public SqlScriptRunner(final Connection connection, final boolean autoCommit, final boolean stopOnError) {
        this(connection, autoCommit, stopOnError, true);
    }

    public SqlScriptRunner(final Connection connection, final boolean autoCommit, final boolean stopOnError, final boolean logToFile) {
        if (connection == null) {
            throw new RuntimeException("Connection is required");
        }
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
        this.logToFile = logToFile;
    }

    public void runScript(final Reader reader) throws SQLException {
        final boolean originalAutoCommit = this.connection.getAutoCommit();
        try {
            if (originalAutoCommit != this.autoCommit) {
                this.connection.setAutoCommit(autoCommit);
            }
            this.runScript(this.connection, reader);
        } finally {
            this.connection.setAutoCommit(autoCommit);
        }
    }

    private void runScript(final Connection connection, final Reader reader) {

        for (String script : formatString(reader)) {
            PreparedStatement statement = null;
            ResultSet rs = null;
            try {
                statement = connection.prepareStatement(script);
                boolean hasResults = false;
                if (stopOnError) {
                    hasResults = statement.execute();
                } else {
                    try {
                        statement.execute();
                    } catch (SQLException ex) {
                        if (logToFile) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        } else {
                            ex.fillInStackTrace();
                        }
                    }
                }

                if (autoCommit) {
                    connection.commit();
                }

            } catch (SQLException ex) {
                if (logToFile) {
                    Logger.getLogger(SqlScriptRunner.class.getName()).log(Level.SEVERE, null, ex);
                } else {
                    ex.fillInStackTrace();
                }
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        if (logToFile) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        } else {
                            ex.fillInStackTrace();
                        }
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException ex) {
                        if (logToFile) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        } else {
                            ex.fillInStackTrace();
                        }
                    }
                }
            }
        }

    }

    private String[] formatString(final Reader reader) {
        String result = "";
        String line;
        final LineNumberReader lineReader = new LineNumberReader(reader);

        try {
            while ((line = lineReader.readLine()) != null) {
                if (line.startsWith("--") || line.startsWith("//") || line.startsWith("#")) {
                    //DO NOTHING - It is a commented line
                } else {
                    result += line;
                }
            }
        } catch (IOException ex) {
            if (logToFile) {
                LOGGER.log(Level.SEVERE, null, ex);
            } else {
                ex.fillInStackTrace();

            }
        }

        if (result == null) {
            throw new RuntimeException("Error while parsing or no scripts in file!");
        } else {
            return result.replaceAll("(?<!"+DEFAULT_SCRIPT_DELIMETER+")(\\r?\\n)+", "").split(DEFAULT_SCRIPT_DELIMETER);
        }
    }
}
