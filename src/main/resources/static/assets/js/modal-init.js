var Modal = {
    "info" : function (info) {
        $('#infoModal .modal-body').html(info);
        this.toggle('infoModal');
    },
    "confirm" : function (info, callback) {
        $('#infoModal .modal-body').html(info);
        $('#confirmModal #confirmBtn').unbind("click");
        $('#confirmModal #confirmBtn').bind("click", function () {
            if (typeof callback === "functon"){
                callback();
            }
        });
        this.toggle('confirmModal');
    },
    "toggle" : function (id) {
        $('#'+id).modal("toggle");
    }
}