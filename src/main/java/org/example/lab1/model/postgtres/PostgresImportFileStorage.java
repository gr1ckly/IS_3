package org.example.lab1.model.postgtres;

import org.example.lab1.entities.dao.ImportFile;
import org.example.lab1.entities.dao.Person;
import org.example.lab1.exceptions.NotFoundException;
import org.example.lab1.model.interfaces.ImportFileStorage;
import org.example.lab1.util.LogCacheMetrics;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PostgresImportFileStorage implements ImportFileStorage {
    private static final String alias = "files";

    private SQLQueryConstraintConverter<ImportFile> queryConverter;

    private SessionFactory sessionFactory;

    @Autowired
    public PostgresImportFileStorage(SQLQueryConstraintConverter<ImportFile> queryConverter, SessionFactory sessionFactory) {
        this.queryConverter = queryConverter;
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public long createImportFile(ImportFile file) throws Exception {
        sessionFactory.getCurrentSession().persist(file);
        return file.getId();
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public ImportFile getFileByID(long id) throws Exception {
        return sessionFactory.getCurrentSession().find(ImportFile.class, id);
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public int getCount() throws Exception {
        int count = 0;
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(");
        query.append(alias);
        query.append(") FROM import_files ");
        query.append(alias);
        Query<?> q = this.queryConverter.buildQuery(sessionFactory.getCurrentSession(), query, alias, null);
        Object res = q.getSingleResult();
        if (res instanceof Number) count = ((Number) res).intValue();
        return count;
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public List<ImportFile> searchImportFiles(int offset, int limit) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM import_files ");
        query.append(alias);
        Query<ImportFile> newQuery = this.queryConverter.buildQuery(sessionFactory.getCurrentSession(), query, alias, ImportFile.class);
        newQuery.setFirstResult(offset);
        newQuery.setMaxResults(limit);
        return newQuery.getResultList();
    }

    @Override
    @Transactional
    @LogCacheMetrics
    public int updateImportFile(long id, ImportFile newFile) throws Exception {
        Session currSession = sessionFactory.getCurrentSession();
        if (currSession.find(ImportFile.class, id) != null ) {
            newFile.setId(id);
            currSession.merge(newFile);
            return 1;
        }else {
            throw new NotFoundException("ImportFile Not Found");
        }
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
