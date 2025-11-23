package org.example.lab1.model.postgtres;

import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.entities.dto.OperationType;
import org.example.lab1.exceptions.NotFoundException;
import org.example.lab1.model.interfaces.LocationStorage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PostgresLocationStorage implements LocationStorage {
    private static final String alias = "loc";

    private SQLQueryConstraintConverter<Location> queryConverter;

    private SessionFactory sessionFactory;

    @Autowired
    public PostgresLocationStorage(SQLQueryConstraintConverter<Location> queryConverter, SessionFactory sessionFactory) {
        this.queryConverter = queryConverter;
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public long createLocation(Location location) throws Exception {
        sessionFactory.getCurrentSession().persist(location);
        return location.getId();
    }

    @Override
    @Transactional
    public Location getLocationByID(long id) throws Exception {
        return sessionFactory.getCurrentSession().find(Location.class, id);
    }

    @Override
    @Transactional
    public int getCount(FilterOption... options) throws Exception {
        int count = 0;
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(");
        query.append(alias);
        query.append(") FROM location ");
        query.append(alias);
        Query<?> q = this.queryConverter.buildQuery(sessionFactory.getCurrentSession(), query, alias, null, options);
        Object res = q.getSingleResult();
        if (res instanceof Number) count = ((Number) res).intValue();
        return count;
    }

    @Override
    @Transactional
    public List<Location> searchLocations(int offset, int limit, FilterOption... options) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM location ");
        query.append(alias);
        Query<Location> newQuery = this.queryConverter.buildQuery(sessionFactory.getCurrentSession(), query, alias, Location.class, options);
        newQuery.setFirstResult(offset);
        newQuery.setMaxResults(limit);
        return newQuery.getResultList();
    }

    @Override
    @Transactional
    public int updateLocation(long id, Location newLocation) throws Exception {
        Session currSession = sessionFactory.getCurrentSession();
        if (currSession.find(Location.class, id) != null){
            newLocation.setId(id);
            currSession.merge(newLocation);
            return 1;
        }else {
            throw new NotFoundException("Location Not Found");
        }
    }

    @Override
    @Transactional
    public int deleteLocation(long id) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM location ");
        query.append(alias);
        return this.queryConverter.buildQuery(sessionFactory.getCurrentSession(), query, alias, Location.class, new FilterOption("id", OperationType.EQUAL, Long.toString(id))).executeUpdate();
    }
}
