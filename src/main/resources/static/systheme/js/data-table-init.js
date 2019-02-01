/**
 * Created by Jackson on 2/1/18.
 */
var reloadTable;
var queryData;
function initQueryParam(aoData){
    var obj = {};
    aoData.forEach(function(e){
        obj[e.name]=e.value;
    });
    var orderColumn = obj.columns[obj.order[0].column].name == null ? ""
        : obj.columns[obj.order[0].column].name;
    var orderdir = obj.order[0].dir;
    var searchValue = obj.search.value;
    var searchRegex = obj.search.regex;
    queryData = {
        orderColumn : orderColumn,
        orderDir : orderdir,
        search : {
            username : $("#username").val(),
            displayName : $("#displayName").val(),
            email : $("#email").val(),
            cellphone : $("#cellphone").val(),
            isLocked : $("#isLocked").val(),
            address : $("#address").val(),
            jobnum : $("#jobnum").val(),
            ipphone : $("#ipphone").val(),
            gender : $("#gender").val()
        },
        start : obj.start,
        length : obj.length,
        searchValue : searchValue,
        searchRegex : searchRegex
    }
    return queryData;
}

$(function() {
    var table = $('.row-details-data-table').DataTable({
        "processing" : true,
        "serverSide" : true,
        "autoWidth" : false,
        "fnServerData" : function(sSource, aoData, fnCallback) {
            var queryData = initQueryParam(aoData);
            $.ajax({
                "type": "POST",
                "url": "listPost",
                "contentType": "application/json",
                "data": JSON.stringify(queryData),
                "success": fnCallback,
                "dataType": "json",
                "error": function(req, textStatus, errorThrown) {

                }
            });
        },
        "dom": '<"tbl-top clearfix"lfr>,t,<"tbl-footer clearfix"<"tbl-info pull-left"i><"tbl-pagin pull-right"p>>',
        "columns": [{
            "class": 'details-control',
            "orderable": false,
            "data": null,
            "defaultContent": ''
        },{
            data : 'username',
            name : 'username'
        },{
            data : 'displayName',
            name : 'display_name'
        },{
            data : 'jobnum',
            name : 'jobnum'
        },{
            data : 'cellphone',
            name : 'cellphone'
        },{
            "createdCell": function (td, cellData, rowData, row, col) {
                $(td).html('<td class="hidden-xs">'+
                    '<button class="btn btn-success btn-xs"><i class="fa fa-check"></i></button>'+
                    '<button class="btn btn-primary btn-xs"><i class="fa fa-pencil"></i></button>'+
                    '<button class="btn btn-danger btn-xs"><i class="fa fa-trash-o "></i></button>'+
                    '</td>');
            },
            "orderable":false,
            "defaultContent": ''
        }],
        /*"order": [
            [1, 'asc']
        ],*/
        "stripeClasses": [ 'strip1', 'strip2', 'strip3' ]
    });

    // Add event listener for opening and closing details
    $('.row-details-data-table tbody').on('click', 'td.details-control', function() {
        var tr = $(this).closest('tr');
        var row = table.row(tr);

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
    reloadTable = function () {
        table.ajax.reload();
    }
});

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
        '<td>d.cellphone</td>' +
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
