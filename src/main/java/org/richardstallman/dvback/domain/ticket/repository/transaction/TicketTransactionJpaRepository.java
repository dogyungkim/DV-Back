package org.richardstallman.dvback.domain.ticket.repository.transaction;

import org.richardstallman.dvback.domain.ticket.entity.TicketTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTransactionJpaRepository
    extends JpaRepository<TicketTransactionEntity, Long> {}
