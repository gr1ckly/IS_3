package org.example.lab1.model.postgtres;

import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dao.Person;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.exceptions.NotFoundException;
import org.example.lab1.model.interfaces.PersonStorage;
import org.example.lab1.util.LogCacheMetrics;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PostgresPersonStorage implements PersonStorage {
    public static final String alias = "pers";

    private SQLQueryConstraintConverter<Person> queryConverter;

    private SessionFactory sessionFactory;

    @Autowired
    public PostgresPersonStorage(SQLQueryConstraintConverter<Person> queryConverter, SessionFactory sessionFactory) {
        this.queryConverter = queryConverter;
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public long createPerson(Person person) throws Exception {
        sessionFactory.getCurrentSession().persist(person);
        return person.getId();
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public Person getPersonByID(long id) throws Exception {
        return sessionFactory.getCurrentSession().find(Person.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    @LogCacheMetrics
    public int getCount(FilterOption... options) throws Exception {
        int count = 0;
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(");
        query.append(alias);
        query.append(") FROM person ");
        query.append(alias);
        Query<?> q = this.queryConverter.buildQuery(sessionFactory.getCurrentSession(), query, alias, null, options);
        Object res = q.getSingleResult();
        if (res instanceof Number) count = ((Number) res).intValue();
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    @LogCacheMetrics
    public List<Person> searchPersons(int offset, int limit, FilterOption... options) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM person ");
        query.append(alias);
        Query<Person> newQuery = this.queryConverter.buildQuery(sessionFactory.getCurrentSession(), query, alias, Person.class, options);
        newQuery.setFirstResult(offset);
        newQuery.setMaxResults(limit);
        return newQuery.getResultList();
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public int updatePerson(long id, Person newPerson) throws Exception {
        Session currSession = sessionFactory.getCurrentSession();
        if (currSession.find(Person.class, id) != null ) {
            newPerson.setId(id);
            currSession.merge(newPerson);
            return 1;
        }else {
            throw new NotFoundException("Person Not Found");
        }
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public int deletePersonByFilter(FilterOption... options) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM person ");
        query.append(alias);
        return this.queryConverter.buildQuery(sessionFactory.getCurrentSession(), query, alias, Person.class, options).executeUpdate();
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public void flush() throws Exception{
        this.sessionFactory.getCurrentSession().flush();
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public void clear() throws Exception{
        this.sessionFactory.getCurrentSession().clear();
    }
}
