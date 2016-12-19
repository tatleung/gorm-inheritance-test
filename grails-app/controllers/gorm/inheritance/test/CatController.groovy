package gorm.inheritance.test

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CatController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Cat.list(params), model:[catCount: Cat.count()]
    }

    def show(Cat cat) {
        respond cat
    }

    def create() {
        respond new Cat(params)
    }

    @Transactional
    def save(Cat cat) {
        if (cat == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (cat.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond cat.errors, view:'create'
            return
        }

        cat.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'cat.label', default: 'Cat'), cat.id])
                redirect cat
            }
            '*' { respond cat, [status: CREATED] }
        }
    }

    def edit(Cat cat) {
        respond cat
    }

    @Transactional
    def update(Cat cat) {
        if (cat == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (cat.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond cat.errors, view:'edit'
            return
        }

        cat.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'cat.label', default: 'Cat'), cat.id])
                redirect cat
            }
            '*'{ respond cat, [status: OK] }
        }
    }

    @Transactional
    def delete(Cat cat) {

        if (cat == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        cat.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'cat.label', default: 'Cat'), cat.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'cat.label', default: 'Cat'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
