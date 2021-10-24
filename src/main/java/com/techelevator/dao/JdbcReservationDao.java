package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservationDao implements ReservationDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        String sql = "INSERT INTO reservation (site_id, name, from_date, to_date) " +
                "VALUES (?, ?, ?, ?) RETURNING reservation_id;";

        return jdbcTemplate.queryForObject(sql, Integer.class,
                siteId, name,  fromDate, toDate);
    }


    @Override
    public List<Reservation> viewReservations(){
        List<Reservation> reservationList = new ArrayList<>();
        String sql = "SELECT reservation_id, site_id,  name, from_date, to_date " +
                "FROM reservation" +
                "JOIN site USING (site_id)" +
                "JOIN campground USING (campground_id)" +
                "JOIN park USING (park_id)" +
                "ORDER BY reservation_id" +
                "WHERE park_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while (results.next()) {
            Reservation reservation = mapRowToReservation(results);
            reservationList.add(reservation);
        }
        return reservationList;
    }


    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(results.getInt("reservation_id"));
        reservation.setSiteId(results.getInt("site_id"));
        reservation.setName(results.getString("name"));
        Date fromDate = results.getDate("from_date");
        if (fromDate != null){
            reservation.setFromDate(fromDate.toLocalDate());
        }
        Date toDate = results.getDate("to_date");
        if (toDate != null){
            reservation.setToDate(toDate.toLocalDate());
        }
        Date createDate = results.getDate("create_date");
        if (createDate != null){
            reservation.setToDate(createDate.toLocalDate());
        }
        return reservation;
    }


}
