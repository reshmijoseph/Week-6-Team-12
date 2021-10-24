package com.techelevator.dao;

import com.techelevator.model.Campground;
import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcSiteDao implements SiteDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcSiteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesThatAllowRVs(int parkId) {
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM site " +
                "JOIN campground USING (campground_id) " +
                "WHERE park_id = ? AND max_rv_length > 0;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while (results.next()) {
            Site site = mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    @Override
    public List<Site> getAvailableSitesByParkId(int parkId) {
        String sql = "SELECT * FROM site " +
                    "JOIN campground ON site.campground_id = campground.campground_id " +
                    "JOIN park ON campground.park_id = park.park_id " +
                    "WHERE park.park_id = ? " +
                    "AND site_id NOT IN (SELECT site.site_id " +
                    "FROM site " +
                    "JOIN reservation ON reservation.site_id = site.site_id " +
                    "WHERE reservation.from_date = CURRENT_DATE OR reservation.from_date = CURRENT_DATE + 1);";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, parkId);

        List<Site> siteList = new ArrayList<>();
        while (result.next()) {
            Site site = mapRowToSite(result);
            siteList.add(site);
        }
        return siteList;
    }

    @Override
    public List<Site> getAvailableSitesInDateRange(int park_id, LocalDate fromDate, LocalDate toDate) {
        String sql = "SELECT * FROM site " +
                "JOIN campground ON site.campground_id = campground.campground_id " +
                "JOIN park ON campground.park_id = park.park_id " +
                "WHERE park.park_id = ? " +
                "AND site_id NOT IN (SELECT site.site_id " +
                "FROM site " +
                "JOIN reservation ON reservation.site_id = site.site_id " +
                "WHERE reservation.from_date <= ? AND reservation.to_date >= ?) ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, park_id, fromDate, toDate);

        List<Site> siteList = new ArrayList<>();
        while (result.next()) {
            Site site = mapRowToSite(result);
            siteList.add(site);
        }
        return siteList;
    }


    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
