package routes

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import repositories.BankRepository
import java.util.*

open class Route {
    open fun handleGet(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        resp.status = HttpServletResponse.SC_METHOD_NOT_ALLOWED
    }

    open fun handlePost(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        resp.status = HttpServletResponse.SC_METHOD_NOT_ALLOWED
    }

    open fun accountUUIDIfAccountExists(
        req: HttpServletRequest,
        resp: HttpServletResponse,
        bankRepository: BankRepository
    ): UUID? {
        val accountUUID = extractUUID(req)
        if (!(checkAccountExistence(resp, bankRepository, accountUUID))) {
            return null
        }
        return accountUUID
    }

    private fun extractUUID(req: HttpServletRequest): UUID {
        val uuidFromRequest = req.requestURI.split("/")[2]
        return UUID.fromString(uuidFromRequest)
    }

    private fun checkAccountExistence(resp: HttpServletResponse, bankRepository: BankRepository, accountUUID: UUID): Boolean {
        if (!bankRepository.accountExists(accountUUID)) {
            resp.status = HttpServletResponse.SC_NOT_FOUND
            return false
        }

        return true
    }
}