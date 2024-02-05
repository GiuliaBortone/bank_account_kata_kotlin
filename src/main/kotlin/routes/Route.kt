package routes

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import repositories.BankRepository

open class Route {
    open fun handleGet(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        resp.status = HttpServletResponse.SC_METHOD_NOT_ALLOWED
    }

    open fun handlePost(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        resp.status = HttpServletResponse.SC_METHOD_NOT_ALLOWED
    }
}