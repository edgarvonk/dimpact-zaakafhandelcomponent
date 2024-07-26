/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.admin.exception

import java.lang.RuntimeException

class ReferenceTableNotFoundException(message: String) : RuntimeException(message)
