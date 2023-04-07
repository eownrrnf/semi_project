package persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import model.*;

public class UserDAO {
	private DataSource ds;
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql = null;
	private static UserDAO dao;

	private UserDAO() {

	}

	public void connect() {
		try {
			Context ct = new InitialContext();
			// "java:comp/env/jdbc/mysql" 현재 웹 어플리케이션의 루트 디렉토리이다.
			ds = (DataSource) ct.lookup("java:comp/env/jdbc/mysql");
			// 마지막으로 데이터 소스를 가져온다.
			con = ds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static UserDAO getInstance() {
		if (dao == null) {
			dao = new UserDAO();
		}
		return dao;
	}

	public void disconnect(ResultSet rs, PreparedStatement pstmt, Connection con) {

		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (con != null)
				con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getListCount() {
		connect();
		int count = 0;
		try {

			sql = "select count(*) from board";
			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}
		return count;
	}

	public boolean registerUser(UserVO vo) {

		try {

			connect();
			String sql = "insert into user values(?,?,?,?,?,?,?)";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, vo.getUser_id());
			pstmt.setString(2, vo.getUser_pwd());
			pstmt.setString(3, vo.getUser_name());
			pstmt.setString(4, vo.getUser_nickname());
			pstmt.setString(5, vo.getUser_email());
			pstmt.setString(6, vo.getUser_phone());
			pstmt.setString(7, vo.getUser_pic());

			pstmt.executeUpdate();

		} catch (SQLException e) {
			
			e.printStackTrace();
			
			return false;
			
		} finally {
			
			disconnect(rs, pstmt, con);
			
		}
		
		return true;
		
	}

	public int logincheck(String id, String pwd) {
		
		connect();
		
		String dbPwd = "";
		
		String dbNickname = "";
		
		UserVO vo = new UserVO();
		
		int check = -1;
		
		this.sql = "SELECT USER_PWD, USER_NICKNAME FROM USER WHERE USER_ID = ?";
		
		try {
			
			this.pstmt = this.con.prepareStatement(this.sql);
			
			this.pstmt.setString(1, id);
			
			this.rs = this.pstmt.executeQuery();
			
			if (this.rs.next()) {
				
				dbPwd = this.rs.getString("user_pwd");
				
				dbNickname = this.rs.getString("user_nickname");
				
				vo.setUser_nickname(dbNickname);
				
				if (dbPwd.equals(pwd)) {
					
					check = 1;
					
				} else {
					
					check = 0;
					
				}
				
			} else {
				
				check = -1;
				
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			
			disconnect(this.rs, this.pstmt, this.con);
			
		}
		return check;
	}

	public String getUserNickname(String id) {
		connect();
		
		String user_nickname = "";
		
		UserVO vo = new UserVO();
		
		try {
			
			sql = "SELECT USER_NICKNAME FROM USER WHERE USER_ID = ?";
			
			this.pstmt = this.con.prepareStatement(this.sql);
			
			this.pstmt.setString(1, id);
			
			this.rs = this.pstmt.executeQuery();
			
			if (this.rs.next()) {
				
				user_nickname = this.rs.getString("user_nickname");
				
				vo.setUser_nickname(user_nickname);
				
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			disconnect(rs, pstmt, con);
			
		}
		return user_nickname;
	}

	public void getMyProfile(String user_nick) {
		// TODO Auto-generated method stub
		
	}

}
