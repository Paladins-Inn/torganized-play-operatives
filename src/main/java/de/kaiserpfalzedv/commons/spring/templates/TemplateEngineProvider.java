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

package de.kaiserpfalzedv.commons.spring.templates;


import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;


/**
 * Creates a template engine for Thymeleaf with correct date and time handling.
 *
 * <p>Should not be necessary anymore, but it isn't active by default.</p>
 *
 * @author klenkes74
 * @since 30.12.24
 */
// FIXME 2024-12-30 rlichti: Move this to kp-commons if it works.
@XSlf4j
public class TemplateEngineProvider {
  public Java8TimeDialect java8TimeDialect() {
    log.entry();
    return log.exit(new Java8TimeDialect());
  }
}
