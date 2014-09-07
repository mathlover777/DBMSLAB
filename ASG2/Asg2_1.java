// To Compile : 
// sourav@ubuntu:~/DBMS/ASG2$ javac -classpath /usr/share/java/mysql.jar Asg2_1.java 
// To Run :
// sourav@ubuntu:~/DBMS/ASG2$ java -classpath .:/usr/share/java/mysql.jar Asg2_1 
// assuming mysql.jar is placed in /usr/share/java/
// otherwise import in eclipse and external mysql.jar Library and execute



import java.sql.*;

public class Asg2_1 {
	/**
	 * @param args
	 */
    public static ResultSet resultQuery(String query) {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); // Initialize the driver
            String url = "jdbc:mysql://10.5.18.71/11CS30037";
            con = DriverManager.getConnection(url, "11CS30037", "password");
            System.out.println("connection Established");
            System.out.println("--------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldnt get connection");
        }

        Statement st = null;
        ResultSet rs = null;
        try {

            st = con.createStatement();
            rs = st.executeQuery(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rs;
    }

	public static void query1() {
//		Query Q = new Query();
		String query;
		query = "SELECT `Name` FROM `Chemical` WHERE `ChemicalNo`"
				+ "IN (	SELECT `ChemicalNo`"
				+ "FROM `Reactant`	WHERE `ReactionNo`	IN (SELECT `ReactionNo`	FROM `Reactant`"
				+ "WHERE `ChemicalNo`	IN (SELECT `ChemicalNo`	FROM `Chemical`	WHERE `Name` LIKE 'Chlorine'"
				+ "))	AND `ChemicalNo` NOT IN (SELECT `ChemicalNo` FROM `Chemical` WHERE `Name` LIKE 'Chlorine'"
				+ "))";
		ResultSet rs = resultQuery(query);
		try {
			rs.last();
			if (rs.getRow() != 0) {
				rs.beforeFirst();
				System.out.println("All chemicals that react with Chlorine:");
				while (rs.next()) {
					System.out.println(rs.getString("Name"));
				}
			} else {
				System.out.println("No Chemical React with Chlorine");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void query2() {
//		Query Q = new Query();
		String query;
		query = "SELECT `Name` FROM `Reaction` WHERE `ReactionNo` IN (	SELECT `ReactionNo`"
				+ "FROM `ReactionCondition` WHERE `ConditionNo` IN ( SELECT `ConditionNo`	FROM `Condition`"
				+ "WHERE `Name` = 'Catalyst'	))";
		ResultSet rs = resultQuery(query);
		try {
			rs.last();
			if (rs.getRow() != 0) {
				rs.beforeFirst();
				System.out
						.println("All Reactions That require Catalyst as condition");
				while (rs.next()) {
					System.out.println(rs.getString("Name"));
				}
			} else {
				System.out.println("None of the reactions require a catalyst!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}

	public static void query3() {
//		Query Q = new Query();
		String query;
		query = "SELECT `Name` FROM `Chemical` WHERE `ChemicalNo` IN (	SELECT `ChemicalNo`"
				+ "FROM (	SELECT `ChemicalNo` , COUNT( `ChemicalNo` ) AS ct FROM `Reactant` GROUP BY `ChemicalNo`"
				+ "ORDER BY ct DESC)y	WHERE ct = (SELECT MAX( ct ) FROM (	SELECT `ChemicalNo` , COUNT( `ChemicalNo` ) AS ct"
				+ " FROM `Reactant` GROUP BY `ChemicalNo` 	ORDER BY ct DESC ) x))";
//		System.out.println(query);
		ResultSet rs = resultQuery(query);
		try {
			rs.last();
			if (rs.getRow() != 0) {
				rs.beforeFirst();
				System.out
						.println("The chemical used in the highest number of reactions");
				while (rs.next()) {
					System.out.println(rs.getString("Name"));
				}
			} else {
				System.out.println("No Result Found!!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}

	public static void query4() {
//		Query Q = new Query();
		String query;
		query = "SELECT `Name` FROM `Chemical` WHERE `ChemicalNo`IN ("
				+ "SELECT `ChemicalNo` FROM `Product` WHERE `ReactionNo`	IN ("
				+ "SELECT `ReactionNo` FROM `Reactant` WHERE `ChemicalNo`	IN ("
				+ "SELECT `ChemicalNo` FROM `Chemical` WHERE `Name` LIKE 'Chlorine'"
				+ ")	))";
//		System.out.println(query);
		ResultSet rs = resultQuery(query);
		try {
			rs.last();
			if (rs.getRow() != 0) {
				rs.beforeFirst();
				System.out
						.println(" All different products that can be produced using Chlorine and any other chemical");
				while (rs.next()) {
					System.out.println(rs.getString("Name"));
				}
			} else {
				System.out.println("No Result Found!!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		query1();
		System.out.print("\n*************************\n");
		query2();
		System.out.print("\n*************************\n");
		query3();
		System.out.print("\n*************************\n");
		query4();
		System.out.print("\n*************************\n");
		return;
	}

}