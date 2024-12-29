/*
 * Copyright (c) 2024. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.operatives;

import de.kaiserpfalzedv.commons.spring.templates.TemplateEngineProvider;
import de.paladinsinn.tp.dcis.commons.events.EnableEventBus;
import de.paladinsinn.tp.dcis.commons.messaging.EnableMessagingConfiguration;
import de.paladinsinn.tp.dcis.commons.rest.EnableRestConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author Roland T. Lichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-06-09
 */
@SpringBootApplication
@EnableEventBus
@EnableRestConfiguration
@EnableMessagingConfiguration
@Import(TemplateEngineProvider.class)
@XSlf4j
public class Application extends SpringApplication {
    @Value("${spring.application.name:OPERATIVES}")
    @Getter(onMethod = @__(@Bean))
    private String applicationName;

    public static void main(String [] args) {
        SpringApplication.run(Application.class, args);
    }

}
