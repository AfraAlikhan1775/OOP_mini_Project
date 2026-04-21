import com.database.DatabaseInitializer;


public class LecturerDAO {

    public LecturerDAO() {
        createTable();
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS lecturer (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    first_name VARCHAR(100),
                    last_name VARCHAR(100),
                    nic VARCHAR(50),
                    dob DATE,
                    gender VARCHAR(10),
                    email VARCHAR(100),
                    phone VARCHAR(20),
                    address TEXT,
                    department VARCHAR(50),
                    specialization VARCHAR(100),
                )
                """;
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            if (rs.next()) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        String sql = """
                """;
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            try (ResultSet rs = pst.executeQuery()) {
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    }
}