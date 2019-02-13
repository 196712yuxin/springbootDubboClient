var dataTable = {
    "dt_columns" : [],
    "dt_search" : {},
    "dt_url" : "",
    "initQueryParam" : function(aoData){
        var obj = {};
        aoData.forEach(function(e){
            obj[e.name]=e.value;
        });
        var orderColumn = obj.columns[obj.order[0].column].name == null ? ""
            : obj.columns[obj.order[0].column].name;
        var orderdir = obj.order[0].dir;
        var searchValue = obj.search.value;
        var searchRegex = obj.search.regex;
        var queryData = {
            orderColumn : orderColumn,
            orderDir : orderdir,
            search : this.dt_search,
            start : obj.start,
            length : obj.length,
            searchValue : searchValue,
            searchRegex : searchRegex
        }
        return queryData;
    },
    "dt_table" : function(){
        var this_parent = this;
        var dt = $('.row-details-data-table').DataTable({
            "processing" : true,
            "serverSide" : true,
            "autoWidth" : false,
            "fnServerData" : function(sSource, aoData, fnCallback) {
                var queryData = this_parent.initQueryParam(aoData);
                $.ajax({
                    "type": "POST",
                    "url": this_parent.dt_url,
                    "contentType": "application/json",
                    "data": JSON.stringify(queryData),
                    "success": fnCallback,
                    "dataType": "json",
                    "error": function(req, textStatus, errorThrown) {

                    }
                });
            },
            "dom": '<"tbl-top clearfix"lfr>,t,<"tbl-footer clearfix"<"tbl-info pull-left"i><"tbl-pagin pull-right"p>>',
            "columns": this.dt_columns,
            "order": [
                [1, 'asc']
            ],
            "stripeClasses": [ 'strip1', 'strip2', 'strip3' ]
        });
        // Add event listener for opening and closing details
        $('.row-details-data-table tbody').on('click', 'td.details-control', function() {
            var tr = $(this).closest('tr');
            var row = dt.row(tr);

            if (row.child.isShown()) {
                // This row is already open - close it
                row.child.hide();
                tr.removeClass('shown');
            } else {
                // Open this row
                row.child(format(row.data())).show();
                tr.addClass('shown');
            }
        });
    },
    "reloadTable" : function () {
        this._datatable.ajax.reload();
    },

    "init" : function (dt_columns, dt_search, dt_url) {
        this.dt_columns = dt_columns;
        this.dt_search = dt_search;
        this.dt_url = dt_url;
        this.dt_table();
    }
}


function format(d) {
    // `d` is the original data object for the row
    return '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">' +
        '<tr>' +
        '<td>Full name:</td>' +
        '<td>' + d.displayName + '</td>' +
        '</tr>' +
        '<tr>' +
        '<td>Email:</td>' +
        '<td>' + d.email + '</td>' +
        '</tr>' +
        '<tr>' +
        '<td>Phone:</td>' +
        '<td>'+ d.cellphone+'</td>' +
        '</tr>' +
        '</table>';
}

$('.convert-data-table').DataTable({
    "PaginationType": "bootstrap",
    dom: '<"tbl-head clearfix"T>,<"tbl-top clearfix"lfr>,t,<"tbl-footer clearfix"<"tbl-info pull-left"i><"tbl-pagin pull-right"p>>',
    tableTools: {
        "sSwfPath": "swf/copy_csv_xls_pdf.swf"
    }
});

$('.colvis-data-table').DataTable({
    "PaginationType": "bootstrap",
    dom: '<"tbl-head clearfix"C>,<"tbl-top clearfix"lfr>,t,<"tbl-footer clearfix"<"tbl-info pull-left"i><"tbl-pagin pull-right"p>>'
});

$('.responsive-data-table').DataTable({
    "PaginationType": "bootstrap",
    responsive: true,
    dom: '<"tbl-top clearfix"lfr>,t,<"tbl-footer clearfix"<"tbl-info pull-left"i><"tbl-pagin pull-right"p>>'
});