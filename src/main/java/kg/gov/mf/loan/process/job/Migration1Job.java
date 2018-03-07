package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.admin.sys.model.User;
import kg.gov.mf.loan.admin.sys.service.UserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;

@Transactional
@Component
public class Migration1Job implements Job{

    @Autowired
    UserService userService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        try {
            Class.forName("org.postgresql.Driver");

        }
        catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/mfloan", "postgres",
                    "nbuser");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;

        }

        if (connection != null) {
            ResultSet rs = null;
            try
            {
                Statement st = connection.createStatement();
                rs = st.executeQuery("SELECT * FROM users where login = 'nso00'");
                if(rs != null)
                {
                    while (rs.next())
                    {
                        System.out.print("Query result returned:  ");
                        System.out.println(rs.getString(1) +" "+ rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5));
                        User user = new User();
                        user.setUsername(rs.getString(2));
                        user.setEnabled(true);
                        user.setPassword("123");
                        userService.create(user);
                    }
                    rs.close();
                    st.close();
                }
            }
            catch (SQLException ex)
            {
                System.out.println("Connection Failed! Check output console");
                ex.printStackTrace();
                return;
            }

        } else {
            System.out.println("Failed to make connection!");
        }

    }
}
