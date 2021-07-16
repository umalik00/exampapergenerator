import java.sql.*;


public class PostgreSQLJDBC {
    Connection a;
    public static String user;
    public static int checkerid;
    public static int setterid;


    public PostgreSQLJDBC() {
        a = null;
        user = null;
    }

    public void main(String args[]) {
        try {
            Class.forName("org.postgresql.Driver");
            this.a = DriverManager
                    .getConnection("jdbc:postgresql://cmpstudb-01.cmp.uea.ac.uk/hza18wtu",
                            "hza18wtu", "X@4n77E2");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        //Connects to database
    }
    public boolean login(String username, String password){
        try {
            Statement s = this.a.createStatement();
            String loginQuery = "SELECT public.user.userid,fullname,user_email,user_password,checkerid,setterid FROM public.user INNER JOIN public.checker ON public.user.userid = checker.userid INNER JOIN public.setter ON setter.userID = public.user.userID  where user_email = '"+username+"' AND user_password = '"+password+"';";
            ResultSet r = s.executeQuery(loginQuery);
            //Database searched for user details
            if(r.next())
            {
                System.out.println("Login Successful");
                user = r.getString("fullname");
                checkerid = r.getInt("checkerID");
                setterid = r.getInt("setterid");
                return true;
            }
            //If log in data correct, user logs into the system, with their data being temporarily saved in the system to be used later (greeting message, checker/setter IDs for creating exam)
            else {
                System.out.println("Unsuccessful");
                return false;
            }
            //If log in data incorrect
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}