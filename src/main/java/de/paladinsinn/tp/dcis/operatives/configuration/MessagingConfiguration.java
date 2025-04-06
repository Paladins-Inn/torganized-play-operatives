/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.paladinsinn.tp.dcis.operatives.configuration;


import de.paladinsinn.tp.dcis.domain.users.services.UserLogEntrySender;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-01-06
 */
@Configuration
@Import(UserLogEntrySender.class)
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@XSlf4j
public class MessagingConfiguration {
    private final UserLogEntrySender userLogEntrySender;
    
    @PostConstruct
    public void init() {
        log.entry(userLogEntrySender);
        log.exit();
    }
}
