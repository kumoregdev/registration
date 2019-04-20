package org.kumoricon.registration.model.tillsession;

import org.kumoricon.registration.model.order.PaymentRepository;
import org.kumoricon.registration.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Service
public class TillSessionService {
    private TillSessionRepository repository;
    private PaymentRepository paymentRepository;

    @Autowired
    public TillSessionService(TillSessionRepository repository, PaymentRepository paymentRepository) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
    }

    public TillSession getCurrentSessionForUser(User user) {
        if (user == null) { throw new RuntimeException("getCurrentSessionForUser called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        if (session == null) {
            session = repository.save(new TillSession(user));
        }
        return session;
    }

    public TillSession getNewSessionForUser(User user) {
        if (user == null) { throw new RuntimeException("getNewSessionForUser called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        if (session != null) {
            closeSession(session);
        }
        session = repository.save(new TillSession(user));
        return session;
    }

    public boolean userHasOpenSession(User user) {
        if (user == null) { throw new RuntimeException("userHasOpenSession called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        return session != null;
    }

    public TillSession closeSessionForUser(User user) {
        if (user == null) { throw new RuntimeException("closeSessionForUser called with null user"); }
        TillSession session = repository.getOpenSessionForUser(user);
        return closeSession(session.getId());
    }

    public TillSession closeSession(TillSession session) {
        if (session != null) {
            if (session.isOpen()) {
                session.setEndTime(Instant.now());
                session.setOpen(false);
                repository.save(session);
            } else {
                throw new RuntimeException(String.format("Session %s is already closed", session));
            }
        }
        return session;
    }

    public TillSession closeSession(Integer id) {
        TillSession session = repository.findOneById(id);
        return closeSession(session);
    }

    public List<TillSession> getAllOpenSessions() {
        return repository.findAllOpenSessions();
    }
    public List<TillSession> getAllSessions() { return repository.findAllOrderByEnd(); }
    public List<TillSessionDTO> getAllTillSessionDTOs() { return repository.findAllTillSessionDTO(); }
    public List<TillSessionDTO> getOpenTillSessionDTOs() { return repository.findOpenTillSessionDTOs(); }

    public BigDecimal getTotalForSession(TillSession s) {
        return paymentRepository.getTotalForSessionId(s.getId());
    }


    public TillSessionDTO getOpenSessionForUser(User currentUser) {
        return repository.getOpenTillSessionDTOforUser(currentUser);
    }

    @Transactional(readOnly = true)
    public TillSessionDetailDTO getTillDetailDTO(Integer id) {
        TillSessionDTO tillSessionDTO = repository.getTillSessionDTOById(id);
        TillSessionDetailDTO detailDTO = TillSessionDetailDTO.fromTillSessionDTO(tillSessionDTO);

        detailDTO.setPaymentTotals(repository.getPaymentTotals(id));
        detailDTO.setBadgeCounts(repository.getBadgeCounts(id));
        detailDTO.setOrderDTOs(repository.getOrderDetails(id));

        return detailDTO;
    }
}
