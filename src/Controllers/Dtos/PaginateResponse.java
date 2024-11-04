package Controllers.Dtos;

import java.util.List;

public class PaginateResponse {

    private List<?> list;
    private Integer total;
    private Integer currentPage;
    private String message;

    public PaginateResponse(List<?> list, Integer total, Integer currentPage, String message) {
        this.list = list;
        this.total = total;
        this.currentPage = currentPage;
        this.message = message;
    }

    public List<?> getList() {
        return list;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
    
    public String getErrorMessage() {
        return this.message;
    }
}
