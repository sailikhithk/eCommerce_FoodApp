/* 
Parse a json file of the trending foods
and display them in the "hot section"
*/
const hot_foods = [
    {
        id: 1,
        name: "Lunch Combo",
        price: "16",
        currency: "USD",
        description: "2 Large hamburgers, 2 medium fries, 2 drumsticks, 1 large coke",
        image_src: "images/food_1.png"
    }
]


function food_template(obj){
    return (
        `
        <li>
            <div class="left"><img src=${obj.photo} alt=""></div>
            <div class="right">
                <p>${obj.name}</p>
                <p>Price <span>$<span>${obj.price}</span></span></p>
                <p>Sold <span>${obj.sold}</span></p>
            </div>
        </li>
        `
    )
}

var urlParams = new URLSearchParams(location.search);
restId = urlParams.get('id');
$.ajax({
    type: "get",
    url: "http://localhost:8080/food/top/"+restId,
    async: true,
    contentType: 'application/json',
    dataType: 'JSON',
    success: function(data) {
        document.getElementById("hot_items").innerHTML = `${data.map(food_template).join("")}`
    }
});

