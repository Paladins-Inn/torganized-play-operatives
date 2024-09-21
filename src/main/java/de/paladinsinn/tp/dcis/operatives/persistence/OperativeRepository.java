/*
 * Copyright (c) 2024 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.paladinsinn.tp.dcis.operatives.persistence;



import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-08-24
 */
public interface OperativeRepository extends JpaRepository<OperativeJPA, Long> {
    OperativeJPA findByUid(UUID uid);
    List<OperativeJPA> findByNameSpace(String owner);
    default List<OperativeJPA> findByOwner(String owner) {
        return findByNameSpace(owner);
    }

    Page<OperativeJPA> findByNameSpace(String owner, Pageable pageable);
    default Page<OperativeJPA> findByOwner(String owner, Pageable pageable) {
        return findByNameSpace(owner, pageable);
    }
}
