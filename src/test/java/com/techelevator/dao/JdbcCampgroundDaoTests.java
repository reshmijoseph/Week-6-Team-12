package com.techelevator.dao;

import com.techelevator.model.Campground;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JdbcCampgroundDaoTests extends BaseDaoTests {

    private CampgroundDao dao;

    @Before
    public void setup() {
        dao = new JdbcCampgroundDao(dataSource);
    }

    @Test
    public void getCampgrounds_Should_ReturnAllCampgrounds() {
        List<Campground> campgrounds = dao.getCampgroundsByParkId(1);

        Assert.assertNotNull(campgrounds);
        Assert.assertEquals(2,campgrounds.size());
    }

}
