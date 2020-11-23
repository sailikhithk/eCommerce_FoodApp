function loadCartList() {
    $.ajax({
        type: "get",
        url: "http://localhost:8080/carts/unpaid",
        async: true,
        contentType: 'application/json',
        dataType: 'JSON',
        success: function(data) {
            renderCartElements(data.cartDetails);
        }
    })
}

function generateCartDiv(obj) {
    var div = $('<div class="dropdown-item">\n' +
        '        <div class="media">\n' +
        '            <img src="' + obj.photo + '" class="img-size-50 mr-3 img-circle">\n' +
        '            <div class="media-body">\n' +
        '                <h3 class="dropdown-item-title">\n' +
        '                    '+ obj.foodName +'\n' +
        '                    <span class="float-right text-sm text-danger"><i class="fas fa-star"></i></span>\n' +
        '                </h3>\n' +
        '                <p class="text-sm"> $ '+ obj.price +'</p>\n' +
        '                <p class="text-sm text-muted"><i class="far fa-clock mr-1"></i> X '+ obj.quantity +' </p>\n' +
        '            </div>\n' +
        '        </div>\n' +
        '    </div>');
    return div;
}
function renderCartElements(data) {
    var root = $('#cart_list');
    root.html("");
    var foodCount = 0;
    $.each(data, function (i, obj) {
        generateCartDiv(obj).appendTo(root)
        if(obj.quantity && obj.quantity > 0){
            $('#'+obj.foodid).text('Add to Cart  [' + obj.quantity + ']');
        }else {
            $('#'+obj.foodid).text('Add to Cart');
        }
        foodCount += obj.quantity;
    });
    $('#foodCount').text(foodCount);
    $('<div class="dropdown-divider"></div>').appendTo(root);
    $('<a href="javascript:void(0);" onclick="checkOut()" class="dropdown-item dropdown-footer">Checkout</a>').appendTo(root);
}

function checkOut() {
    var userId = window.localStorage.getItem('userid');
    var obj = {
        userid: userId
    };
    $.ajax({
        type: "post",
        url: "http://localhost:8080/carts/checkout",
        async: true,
        contentType: 'application/json',
        dataType: 'JSON',
        data: JSON.stringify(obj),
        success: function(data) {
            var stripe = Stripe(data.publicKey);
            stripe.redirectToCheckout({
                sessionId: data.sessionId,
            });
        }
    });
}