package persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import model.*;

public class MusicDAO {

	private DataSource ds;

	Connection con = null;

	PreparedStatement pstmt = null;

	ResultSet rs = null;

	String sql = null;

	private static MusicDAO dao;

	public MusicDAO() {

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

	public static MusicDAO getInstance() {

		if (dao == null) {
			dao = new MusicDAO();
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

			e.printStackTrace();

		}

	}

	public int insertFile(MusicVO vo) {

		connect();

		int result = 0;

		int count = 0;

		try {

			sql = "insert into music_info values(default,?,?,?,?,?,?,?,?) ";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, vo.getMusic_pic());

			pstmt.setString(2, vo.getMusic_mp3());

			pstmt.setString(3, vo.getMusic_title());

			pstmt.setString(4, vo.getMusic_contents());

			pstmt.setInt(5, 0);

			pstmt.setInt(6, 0);

			pstmt.setString(7, vo.getUser_id());

			pstmt.setString(8, vo.getUser_nickname());

			result = pstmt.executeUpdate();

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			disconnect(rs, pstmt, con);

		}

		return result;

	} // insertUpload() 메서드 end

	public List<MusicVO> getMusicList() {

		connect();

		List<MusicVO> list = new ArrayList<MusicVO>();

		try {

			sql = "select * from music_info";

			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				MusicVO vo = new MusicVO();

				vo.setMusic_id(rs.getInt("music_id"));
				vo.setMusic_pic(rs.getString("music_pic"));
				vo.setMusic_mp3(rs.getString("music_mp3"));
				vo.setMusic_title(rs.getString("music_title"));
				vo.setMusic_contents(rs.getString("music_contents"));
				vo.setMusic_likecnt(rs.getInt("music_likecnt"));
				vo.setMusic_playcnt(rs.getInt("music_playcnt"));
				vo.setUser_nickname(rs.getString("user_nickname"));
				vo.setUser_id(rs.getString("user_id"));

				list.add(vo);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}
		return list;
	} // getMusicList() end

	public MusicVO contentMusic(int album_id) {
		connect();
		MusicVO vo = null;
		try {
			sql = "select * from music_info where music_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, album_id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				vo = new MusicVO();
				vo.setMusic_id(rs.getInt("music_id"));
				vo.setMusic_contents(rs.getString("music_contents"));
				vo.setMusic_mp3(rs.getString("music_mp3"));
				vo.setMusic_pic(rs.getString("music_pic"));
				vo.setMusic_title(rs.getString("music_title"));
				vo.setMusic_likecnt(rs.getInt("music_likecnt"));
				vo.setMusic_playcnt(rs.getInt("music_playcnt"));
				vo.setUser_id(rs.getString("user_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}
		return vo;
	}

	public List<MusicVO> getMyMusicList(String user_id) {

		connect();

		List<MusicVO> list = new ArrayList<MusicVO>();
		try {
			sql = "select * from music_info where user_id =?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				MusicVO vo = new MusicVO();

				vo.setMusic_id(rs.getInt("music_id"));
				vo.setMusic_pic(rs.getString("music_pic"));
				vo.setMusic_mp3(rs.getString("music_mp3"));
				vo.setMusic_title(rs.getString("music_title"));
				vo.setMusic_contents(rs.getString("music_contents"));
				vo.setMusic_likecnt(rs.getInt("music_likecnt"));
				vo.setMusic_playcnt(rs.getInt("music_playcnt"));
				vo.setUser_id(rs.getString("user_id"));

				list.add(vo);

			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			disconnect(rs, pstmt, con);

		}

		return list;

	}

	public MusicLikesVO getMusicLikesByUserIdAndMusicId(String user_id, int music_id) throws SQLException {
		connect();

		MusicLikesVO musicLikes = null;

		try {

			String sql = "SELECT * FROM music_likes WHERE user_id=? AND music_id=?";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, user_id);

			pstmt.setInt(2, music_id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				musicLikes = new MusicLikesVO();
				musicLikes.setId(rs.getInt("id"));
				musicLikes.setUser_id(rs.getString("user_id"));
				musicLikes.setMusic_id(rs.getInt("music_id"));
				musicLikes.setIs_liked(rs.getBoolean("is_liked"));
				musicLikes.setIs_disliked(rs.getBoolean("is_disliked"));
			}
		} finally {
			disconnect(rs, pstmt, con);
		}

		return musicLikes;
	}

	public boolean toggleLike(MusicLikesVO musicLikes) throws SQLException {
		connect();
		boolean result = false;

		try {
			if (musicLikes.getId() == 0) {
				String sql = "INSERT INTO music_likes (user_id, music_id, is_liked, is_disliked) VALUES (?, ?, ?, ?)";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, musicLikes.getUser_id());
				pstmt.setInt(2, musicLikes.getMusic_id());
				pstmt.setBoolean(3, musicLikes.isIs_liked());
				pstmt.setBoolean(4, musicLikes.isIs_disliked());
			} else {
				String sql = "UPDATE music_likes SET is_liked=?, is_disliked=? WHERE id=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setBoolean(1, musicLikes.isIs_liked());
				pstmt.setBoolean(2, musicLikes.isIs_disliked());
				pstmt.setInt(3, musicLikes.getId());
			}

			result = pstmt.executeUpdate() > 0;
		} finally {
			disconnect(rs, pstmt, con);
		}

		return result;
	}

	public boolean updateMusic(MusicVO vo) {

		connect();

		boolean isUpdated = false;

		try {

			sql = "UPDATE music_info SET music_pic = ?, music_mp3 = ?, music_title = ?, music_contents = ?, music_likecnt = ?, music_playcnt = ?, user_id = ? WHERE music_id = ?";
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, vo.getMusic_pic());
			pstmt.setString(2, vo.getMusic_mp3());
			pstmt.setString(3, vo.getMusic_title());
			pstmt.setString(4, vo.getMusic_contents());
			pstmt.setInt(5, vo.getMusic_likecnt());
			pstmt.setInt(6, vo.getMusic_playcnt());
			pstmt.setString(7, vo.getUser_id());
			pstmt.setInt(8, vo.getMusic_id());

			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {

				isUpdated = true;

			}

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			disconnect(rs, pstmt, con);

		}

		return isUpdated;
	}

	public Map<String, Object> getLikeStatus(String user_id, int music_id) {
		connect();
		Map<String, Object> likeStatus = new HashMap<>();
		MusicVO music = null;
		MusicLikesVO musicLikes = null;

		try {

			sql = "SELECT * FROM music_info WHERE music_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, music_id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				music = new MusicVO();
				music.setMusic_id(rs.getInt("music_id"));
				music.setMusic_likecnt(rs.getInt("music_likecnt"));
				music.setMusic_playcnt(rs.getInt("music_playcnt"));
			}

			sql = "SELECT * FROM music_likes WHERE user_id = ? AND music_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, user_id);
			pstmt.setInt(2, music_id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				musicLikes = new MusicLikesVO();
				musicLikes.setId(rs.getInt("id"));
				musicLikes.setUser_id(rs.getString("user_id"));
				musicLikes.setMusic_id(rs.getInt("music_id"));
				musicLikes.setIs_liked(rs.getBoolean("is_liked"));
				musicLikes.setIs_disliked(rs.getBoolean("is_disliked"));
			}

			likeStatus.put("music", music);
			likeStatus.put("musicLikes", musicLikes);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}

		return likeStatus;
	}

	public MusicVO musicModify(int music_id) {

		MusicVO vo = null;

		try {

			connect();

			sql = "select * from music_info where music_id = ?";

			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, music_id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				vo = new MusicVO();
				vo.setMusic_id(rs.getInt("music_id"));
				vo.setMusic_pic(rs.getString("music_pic"));
				vo.setMusic_mp3(rs.getString("music_mp3"));
				vo.setMusic_title(rs.getString("music_title"));
				vo.setMusic_contents(rs.getString("music_contents"));
				vo.setMusic_likecnt(rs.getInt("music_likecnt"));
				vo.setMusic_playcnt(rs.getInt("music_playcnt"));
				vo.setUser_id(rs.getString("user_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}
		return vo;
	} // musicModify() 메서드 end

	// 상세페이지에서 수정 페이지로 가는 메서드
	public MusicVO modifyOkDo(String music_id) {

		MusicVO vo = new MusicVO();

		try {
			connect();

			sql = "select * from music_info where music_id = ?";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, music_id);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				vo.setMusic_id(rs.getInt("music_id"));
				vo.setMusic_pic(rs.getString("music_pic"));
				vo.setMusic_mp3(rs.getString("music_mp3"));
				vo.setMusic_title(rs.getString("music_title"));
				vo.setMusic_contents(rs.getString("music_contents"));
				vo.setMusic_likecnt(rs.getInt("music_likecnt"));
				vo.setMusic_playcnt(rs.getInt("music_playcnt"));
				vo.setUser_id(rs.getString("user_id"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}
		return vo;
	}

	// 게시한 앨범을 삭제.
	public int deleteMusic(int music_id) {

		connect();

		int result = 0;

		try {
			// 앨범에 달린 댓글을 삭제
			sql = "DELETE FROM comments WHERE album_id = ?";

			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, music_id);

			result = pstmt.executeUpdate();

			// 앨범을 삭제
			sql = "DELETE FROM MUSIC_INFO WHERE music_id = ?";

			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, music_id);

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}

		return result;
	} // 앨범을 삭제하는 메서드 end

	public void updateSequence(int music_id) {

		connect();

		try {
			sql = "update music_info set music_id = music_id -1 where music_id > ?";

			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, music_id);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}
	}

	// 조회수 증가
	public void musicPlayCnt(int music_id) {

		try {
			connect();

			sql = "UPDATE music_info " + "SET music_playcnt = music_playcnt + 1 " + "where music_id = ?";

			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, music_id);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}
	} // 조회수 증가 메서드 end

	public int updateModifyMusic(MusicVO vo) {

		connect();

		int result = 0;

		try {
			sql = "update music_info set music_contents = ?, music_likecnt = ?, music_playcnt = ?, music_title = ?, music_pic = ? WHERE music_id = ?";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, vo.getMusic_contents());
			pstmt.setInt(2, vo.getMusic_likecnt());
			pstmt.setInt(3, vo.getMusic_playcnt());
			pstmt.setString(4, vo.getMusic_title());
			pstmt.setString(5, vo.getMusic_pic());
			pstmt.setInt(6, vo.getMusic_id());

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}

		return result;
	} // 음악 수정 업데이트 메서드 end

	public boolean savePlayedMusic(PlayedMusicVO vo) {
		connect();

		boolean result = false;

		try {
			sql = "INSERT INTO played_music (user_id, music_id, play_time) VALUES (?, ?, default)";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, vo.getUser_id());
			pstmt.setInt(2, vo.getMusic_id());

			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}

		return result;
	}

	// id에 맞는 플레이리스트를 가져오는 메서드
	public List<LikelistVO> getMyPlayListshow(String id) {
		List<LikelistVO> list = new ArrayList<LikelistVO>();
		LikelistVO vo = new LikelistVO();
		connect();

		try {
			sql = "select * from myplaylist where user_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				vo = new LikelistVO();

				vo.setPlaylist_id(rs.getInt("playlist_id"));
				vo.setUser_id(rs.getString("user_id"));
				vo.setMusic_id(rs.getInt("music_id"));
				vo.setPlaylist(rs.getInt("playlist"));
				vo.setPlaylist_name(rs.getString("playlist_name"));

				list.add(vo);

			}

		} catch (

		SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}

		return list;

	} // getMyPlayListshow() 메서드 end

	// 홈페이지 창에서 검색을 하는 메서드
	public ArrayList<MusicVO> searchMusicList(String search, String keyword) {

		ArrayList<MusicVO> list = new ArrayList<MusicVO>();

		connect();

		if (search.equals("All")) {

			try {
				sql = "SELECT * FROM music_info WHERE user_nickname LIKE CONCAT('%', ?, '%') or "
						+ " music_title LIKE CONCAT('%', ?, '%') or " + " music_contents LIKE CONCAT('%', ?, '%') "
						+ " order by music_id desc";

				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, keyword);
				pstmt.setString(2, keyword);
				pstmt.setString(3, keyword);

				rs = pstmt.executeQuery();

				while (rs.next()) {

					MusicVO vo = new MusicVO();

					vo.setMusic_id(rs.getInt("music_id"));
					vo.setMusic_pic(rs.getString("music_pic"));
					vo.setMusic_mp3(rs.getString("music_mp3"));
					vo.setMusic_title(rs.getString("music_title"));
					vo.setMusic_contents(rs.getString("music_contents"));
					vo.setMusic_likecnt(rs.getInt("music_likecnt"));
					vo.setMusic_playcnt(rs.getInt("music_playcnt"));
					vo.setUser_nickname(rs.getString("user_nickname"));
					vo.setUser_id(rs.getString("user_id"));

					list.add(vo);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				disconnect(rs, pstmt, con);
			}
		} else if (search.equals("name")) {

			try {

				sql = "SELECT * FROM music_info WHERE user_nickname LIKE CONCAT('%', ?, '%') order by music_id desc";

				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, keyword);

				rs = pstmt.executeQuery();

				while (rs.next()) {

					MusicVO vo = new MusicVO();

					vo.setMusic_id(rs.getInt("music_id"));
					vo.setMusic_pic(rs.getString("music_pic"));
					vo.setMusic_mp3(rs.getString("music_mp3"));
					vo.setMusic_title(rs.getString("music_title"));
					vo.setMusic_contents(rs.getString("music_contents"));
					vo.setMusic_likecnt(rs.getInt("music_likecnt"));
					vo.setMusic_playcnt(rs.getInt("music_playcnt"));
					vo.setUser_nickname(rs.getString("user_nickname"));
					vo.setUser_id(rs.getString("user_id"));

					list.add(vo);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				disconnect(rs, pstmt, con);
			}
		} else if (search.equals("title")) {
			System.out.println("title검색 진입");
			try {

				sql = "SELECT * FROM music_info WHERE music_title LIKE CONCAT('%', ?, '%') order by music_id desc";

				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, keyword);

				rs = pstmt.executeQuery();

				while (rs.next()) {

					MusicVO vo = new MusicVO();

					vo.setMusic_id(rs.getInt("music_id"));
					vo.setMusic_pic(rs.getString("music_pic"));
					vo.setMusic_mp3(rs.getString("music_mp3"));
					vo.setMusic_title(rs.getString("music_title"));
					vo.setMusic_contents(rs.getString("music_contents"));
					vo.setMusic_likecnt(rs.getInt("music_likecnt"));
					vo.setMusic_playcnt(rs.getInt("music_playcnt"));
					vo.setUser_nickname(rs.getString("user_nickname"));
					vo.setUser_id(rs.getString("user_id"));

					list.add(vo);
					System.out.println("title검색 출력 >>." + vo);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				disconnect(rs, pstmt, con);
			}

		} else if (search.equals("content")) {

			try {
				sql = "SELECT * FROM music_info WHERE music_contents LIKE CONCAT('%', ?, '%') order by music_id desc";

				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, keyword);

				rs = pstmt.executeQuery();

				while (rs.next()) {

					MusicVO vo = new MusicVO();

					vo.setMusic_id(rs.getInt("music_id"));
					vo.setMusic_pic(rs.getString("music_pic"));
					vo.setMusic_mp3(rs.getString("music_mp3"));
					vo.setMusic_title(rs.getString("music_title"));
					vo.setMusic_contents(rs.getString("music_contents"));
					vo.setMusic_likecnt(rs.getInt("music_likecnt"));
					vo.setMusic_playcnt(rs.getInt("music_playcnt"));
					vo.setUser_nickname(rs.getString("user_nickname"));
					vo.setUser_id(rs.getString("user_id"));

					list.add(vo);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				disconnect(rs, pstmt, con);
			}
		}
		return list;
	}

	public List<PlayedMusicVO> getPlayedMusicList(String sessionId) {
		connect();

		List<PlayedMusicVO> list = new ArrayList<>();

		try {
			String sql = "SELECT * FROM played_music pm JOIN music_info mi ON pm.music_id = mi.music_id WHERE pm.user_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, sessionId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				PlayedMusicVO vo = new PlayedMusicVO();
				vo.setPlay_id(rs.getInt("play_id"));
				vo.setUser_id(rs.getString("user_id"));
				vo.setMusic_id(rs.getInt("music_id"));
				vo.setPlay_time(rs.getTimestamp("play_time"));
				vo.setMusic_title(rs.getString("music_title"));
				vo.setMusic_pic(rs.getString("music_pic"));
				vo.setMusic_mp3(rs.getString("music_mp3"));
				vo.setMusic_contents(rs.getString("music_contents"));
				vo.setMusic_likecnt(rs.getInt("music_likecnt"));
				vo.setMusic_playcnt(rs.getInt("music_playcnt"));
				vo.setUser_nickname(rs.getString("user_nickname"));
				list.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}

		return list;
	}

	// user_id로 음악 리스트 가져오기
	public List<MusicVO> getUserMusicList(String user_id) {

		connect();

		List<MusicVO> list = new ArrayList<MusicVO>();

		try {

			sql = "select * from music_info where user_id = ?";

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, user_id);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				MusicVO vo = new MusicVO();

				vo.setMusic_id(rs.getInt("music_id"));
				vo.setMusic_pic(rs.getString("music_pic"));
				vo.setMusic_mp3(rs.getString("music_mp3"));
				vo.setMusic_title(rs.getString("music_title"));
				vo.setMusic_contents(rs.getString("music_contents"));
				vo.setMusic_likecnt(rs.getInt("music_likecnt"));
				vo.setMusic_playcnt(rs.getInt("music_playcnt"));
				vo.setUser_nickname(rs.getString("user_nickname"));
				vo.setUser_id(rs.getString("user_id"));

				list.add(vo);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}
		return list;
	} // getMusicList() end

	public int getUserTrackCount(String bbs_id) {

		connect();

		int result = 0;

		try {
			sql = "SELECT COUNT(user_id) FROM music_info WHERE user_id = ?";

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bbs_id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}

		return result;

	}

	public List<MusicVO> getFeedList(String sessionId) {

		connect();

		System.out.println("dao session id > " + sessionId);

		List<MusicVO> list = new ArrayList<MusicVO>();

		try {
			sql = "SELECT * FROM music_info LEFT JOIN follows on music_info.user_id = follows.followed_id WHERE follows.follower_id = ? ORDER BY music_info.music_id desc";

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, sessionId);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				MusicVO vo = new MusicVO();

				vo.setMusic_id(rs.getInt("music_id"));
				vo.setMusic_pic(rs.getString("music_pic"));
				vo.setMusic_mp3(rs.getString("music_mp3"));
				vo.setMusic_title(rs.getString("music_title"));
				vo.setMusic_contents(rs.getString("music_contents"));
				vo.setMusic_likecnt(rs.getInt("music_likecnt"));
				vo.setMusic_playcnt(rs.getInt("music_playcnt"));
				vo.setUser_nickname(rs.getString("user_nickname"));
				vo.setUser_id(rs.getString("user_id"));

				list.add(vo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect(rs, pstmt, con);
		}

		return list;
	}

}
