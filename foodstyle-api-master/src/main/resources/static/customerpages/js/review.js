let ratings = ["one", "two", "three", "four", "five"]

ratings.forEach(element => {
    document.getElementById(element).addEventListener("click",
    function(){
        let below = true
        let class_name
        for(var i=0; i < ratings.length; i++){
            if (ratings[i] === element){
                below = false
                class_name = document.getElementById(element).className
                if (class_name.includes("unchecked")){
                    document.getElementById(element).classList.remove("unchecked")
                    document.getElementById(element).classList.add("checked")
                }
                // set value
                document.getElementById("ratingScore").setAttribute('value', i+1)
            }
            else if (below){
                curr_id = ratings[i]
                class_name = document.getElementById(curr_id).className
                if (class_name.includes("unchecked")){
                    document.getElementById(curr_id).classList.remove("unchecked")
                    document.getElementById(curr_id).classList.add("checked")
                }
            }
            else{
                curr_id = ratings[i]
                class_name = document.getElementById(curr_id).className
                if (class_name.includes("checked")){
                    document.getElementById(curr_id).classList.remove("checked")
                    document.getElementById(curr_id).classList.add("unchecked")
                }
            }
        }  
        
    })
});
function loadReviewData() {
    var urlParams = new URLSearchParams(location.search);
    restId = urlParams.get('id');
    restaurantName = urlParams.get('name');
    $('#rest_name').text(restaurantName);

    $.ajax({
        type: "get",
        url: "http://localhost:8080/reviews/restaurant//"+restId,
        async: true,
        contentType: 'application/json',
        dataType: 'JSON',
        success: function(data) {
            $('#commentCount').text(data.length);
            var userId = window.localStorage.getItem('userid');
            var reviewList = $('#reviewList');
            reviewList.html('');
            $.each(data, function (i, obj) {
                if (obj.userid == userId){
                    generateRightDiv(i,obj).appendTo(reviewList);
                    $('#'+i).html(getRating(obj.rating));
                }else {
                    generateLeftDiv(i, obj).appendTo(reviewList);
                    $('#'+i).html(getRating(obj.rating));
                }
            });
        }
    });
}

function generateLeftDiv(i, obj) {
    var div = $('<div class="direct-chat-msg">\n' +
'                        <div class="direct-chat-infos clearfix">\n' +
'                            <span class="direct-chat-name float-left">'+ (obj.username.charAt(0) + "****" + obj.username.charAt(obj.username.length-1)) + '<div id="'+ i +'"></div></span>\n' +
'                            <span class="direct-chat-timestamp float-right">'+ obj.reviewtime.replace("T", " ").replace("Z", "") +'</span>\n' +
'                        </div>\n' +
'                        <img class="direct-chat-img" src="../managerpages/dist/img/user1-128x128.jpg" alt="message user image">\n' +
'                        <div class="direct-chat-text">\n' +
'                            '+ obj.review +'\n' +
'                        </div>\n' +
'                    </div>');
    return div;
}

function generateRightDiv(i, obj){
    var div = $('<div class="direct-chat-msg right">\n' +
'                        <div class="direct-chat-infos clearfix">\n' +
'                            <span class="direct-chat-name float-right">You<div id="'+ i +'"></div></span>\n' +
'                            <span class="direct-chat-timestamp float-left">'+ obj.reviewtime.replace("T", " ").replace("Z", "") +'</span>\n' +
'                        </div>\n' +
'                        <img class="direct-chat-img" src="../managerpages/dist/img/user3-128x128.jpg" alt="message user image">\n' +
'                        <div class="direct-chat-text">\n' +
'                            '+ obj.review +'\n' +
'                        </div>\n' +
'                    </div>');

    return div;
}

function getRating(ratting) {
    var div = $('<div>\n' +
'                        <i class="fa fa-star '+ (ratting >= 1 ? "checked" : "unchecked") +'" ></i>\n' +
'                        <i class="fa fa-star '+ (ratting >= 2 ? "checked" : "unchecked") +'" ></i>\n' +
'                        <i class="fa fa-star '+ (ratting >= 3 ? "checked" : "unchecked") +'" ></i>\n' +
'                        <i class="fa fa-star '+ (ratting >= 4 ? "checked" : "unchecked") +'" ></i>\n' +
'                        <i class="fa fa-star '+ (ratting >= 5 ? "checked" : "unchecked") +'" ></i>\n' +
'                    </div>');
    return div;
}