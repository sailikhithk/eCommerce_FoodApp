function addCart(obj) {
    var foodId = obj.id;
    var userId = window.localStorage.getItem('userid');
    if (!userId){
        alert('You have not logged in!');
        return;
    }
    var obj = {
        userid: userId,
        foodid: foodId
    };
    $.ajax({
        type: "post",
        url: "http://localhost:8080/carts/details",
        async: true,
        contentType: 'application/json',
        dataType: 'JSON',
        data: JSON.stringify(obj),
        success: function(data) {
            $('#'+foodId).text('Add to Cart  [' + data + ']');
            loadCartList();
        }
    });
}
function generateLi(obj, foodName) {
    var li = $('<li></li>');
    var div_1 = $('<div class="img"></div>');
    var img = $('<img src=""/>');
    img.attr('src', obj.photo);
    img.appendTo(div_1);

    var div_2 = $('<div class="text1"></div>');
    var p_name = $('<p></p>');
    p_name.text(obj.name);

    var p_des = $('<p>'+ obj.description +'</p>');
    p_name.appendTo(div_2);
    $('<br/>').appendTo(div_2);
    p_des.appendTo(div_2);

    var p_sold = $('<p></p>');
    p_sold.text('Sold ('+obj.sold+')');
    p_sold.appendTo(div_2);

    var div_3 = $('<div class="price"></div>');
    var p_price = $('<p class="price_num"><span>$</span><span>'+ obj.price +'</span></p>');
    var p_detail = $('<p></p>');
    var a_detail = $('<a class="btn btn-primary" onclick="addCart(this)" style="">Add to Cart</a>');
    a_detail.attr('id', obj.id);
    a_detail.appendTo(p_detail);
    p_price.appendTo(div_3);
    p_detail.appendTo(div_3);

    div_1.appendTo(li);
    div_2.appendTo(li);
    div_3.appendTo(li);

    return li;
}
function renderElements(data, foodName) {
    var root = $('#food_list');
    root.html("");
    var ul = $('<ul></ul>');
    $.each(data, function (i, obj) {
        generateLi(obj, foodName).appendTo(ul)
    });
    ul.appendTo(root);
}
function loadData(){
    var urlParams = new URLSearchParams(location.search);
    restId = urlParams.get('id');
    restaurantName = urlParams.get('restaurantName');
    $('#restaurantName').text(restaurantName);
    $.ajax({
        type: "get",
        url: "http://localhost:8080/food/restaurant/"+restId,
        async: true,
        contentType: 'application/json',
        dataType: 'JSON',
        success: function(data) {
            renderElements(data);
        }
    });
}

function searchFood() {
    var foodName = $('#searchValue').val();
    if (foodName.trim() == ''){
        return;
    }
    var obj = {
        restaurantId: restId,
        foodName: foodName
    };
    $.ajax({
        type: "post",
        url: "http://localhost:8080/food/search",
        async: true,
        contentType: 'application/json',
        dataType: 'JSON',
        data: JSON.stringify(obj),
        success: function(data) {
            renderElements(data, foodName);
        }
    });
}

function viewComments() {
    window.open("http://localhost:8080/customerpages/review.html?id="+restId+"&name="+restaurantName, 'blank');
}
