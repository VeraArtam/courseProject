package helpers;

import java.sql.*;

public class DbHelper {

    private static final String URL = "jdbc:mysql://localhost:3306/aqa_shop";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String getLastPaymentStatus() {
        String status = null;
        String sql = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static int getPaymentCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM payment_entity";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static String getLastCreditStatus() {
        String status = null;
        String sql = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static boolean paymentTableHasNoCardData() {
        String sql = "SHOW COLUMNS FROM payment_entity";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String column = rs.getString("Field").toLowerCase();
                if (column.contains("card") || column.contains("cvc") || column.contains("cvv")
                        || column.contains("owner") || column.contains("holder")) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static int getOrderCount() {
        String sql = "SELECT COUNT(*) FROM order_entity";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
