/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.ejb.sb;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.foi.nwtis.zorhrncic.ejb.eb.Dnevnik;

/**
 *
 * @author grupa_1
 */
@Stateless(name = "PlaceBidBeanLocal")
public class DnevnikFacade extends AbstractFacade<Dnevnik> {

    @PersistenceContext(unitName = "zadaca_4_1PU")
    private EntityManager em;
    private Predicate predicate3;
    private Predicate predicate2;
    private Predicate predicate1;
    private Predicate predicate;
    private Predicate predicate0;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DnevnikFacade() {
        super(Dnevnik.class);
    }

    
    /**
     * Dohvaca podateke iz tablice "Dnevnike" na temelju unešenih podataka za pretreaživanje.
     * Mogu biti unjeti bilo koji od 5 podataka iz tablice.
     * @param ipv6
     * @param from
     * @param to
     * @param duration
     * @param url
     * @return 
     */
    public List<Dnevnik> getByFilter(String ipv6, Date from, Date to, int duration, String url) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
// Query for a List of objects.
        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(Dnevnik.class);
        createPredicates(cb, duration, e, url, ipv6, from, to);
        cq.where(cb.and(predicate0,
                predicate1,
                predicate2,
                predicate3,
                predicate
        )
        );
        Query query = em.createQuery(cq);
        List<Dnevnik> result = query.getResultList();

        return result;
    }

    
    /**
     * Kreiranje predikata prema kojima se pretrazuje baza podataka; tablica "Dnevnik"
     * 
     * @param cb
     * @param duration
     * @param e
     * @param url
     * @param ipv6
     * @param from
     * @param to 
     */
    private void createPredicates(CriteriaBuilder cb, int duration, Root e, String url, String ipv6, Date from, Date to) {
        predicate0 = cb.conjunction();
        predicate = cb.conjunction();
        predicate1 = cb.conjunction();
        predicate2 = cb.conjunction();
        predicate3 = cb.conjunction();

        if (duration != 0) {
            predicate0 = cb.equal(e.get("trajanje"), duration);
        }
        if (!url.isEmpty()) {
            predicate = cb.equal(e.get("url"), url);
        }
        if (!ipv6.isEmpty()) {
            predicate1 = cb.equal(e.get("ipadresa"), ipv6);
        }
        if (from != null) {
            predicate2 = cb.greaterThan(e.get("vrijeme"), from);
        }
        if (to != null) {
            predicate3 = cb.lessThan(e.get("vrijeme"), to);
        }
    }

}
