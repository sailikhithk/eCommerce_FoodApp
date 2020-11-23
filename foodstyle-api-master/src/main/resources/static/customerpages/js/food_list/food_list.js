
function foodlist_template(obj){
    return (
        `
        <li id=${obj.id}>
            <img class="img" src=${obj.photo} />
            <div class="text1">
                <p>${obj.name}</p>
                <p>${obj.description}</p>
            </div>
            <div class="price">
                <p class="price_num">
                    <span>$${obj.price}</span>
                </p>
                <p><a href="food_detail.html">view detail</a></p>
            </div>
        </li>
        `
    )
}

// Display a list of uniq food categories
function food_categories(category){
    return(
        `
        <a class="food-category" id="${category}-scroll" >${category}</a>
        `
    )
}

// Get all the uniq categories from the json_data
function get_uniq_categories(array){
    let seen = new Set();
    for (let i =0; i < array.length; i++){
        if (! (array[i].category in seen) && (array[i].category) !== null){
            seen.add(array[i].category)
        }
    }
    return Array.from(seen)
}

// separate the foods to their pertaining category
function get_foods_by_category(food_data){
    let my_dict = {}
    for (let i = 0; i < food_data.length; i++){
        let category = food_data[i].category
        if (category){ // if not null
            // Add object to that category
            if (category in my_dict){
                my_dict[category].push(food_data[i])
            }
            // set category as key
            else{
                my_dict[category] = [food_data[i]]
            }
        }
    }
    return my_dict
}

// Get food list data from api
console.log('outside')
$.ajax({
    url: "http://localhost:8080/food-list",
    type: 'GET',
    dataType: 'json',
    success: function(data) {
        console.log(data);
        console.log('here I am');
        let food_data = data;
        // Add correct path to all images
        for(var i=0; i < food_data.length; i++){
            food_data[i].photo = "images/" + food_data[i].photo
        }

        console.log(food_data)

        // Display categories in the category-list section
        let uniq_categories = get_uniq_categories(food_data)
        console.log('uniq_cat:',uniq_categories)
        document.getElementById("food-category-list").innerHTML = `${uniq_categories.map(food_categories).join("")}`

        // Display a list of food under their pertaining categories
        let category_dict = get_foods_by_category(food_data)
        document.getElementById("food_items").innerHTML = `${
            Object.keys(category_dict).map(function(category, index) {
                return (
                    `
                    <div class='left'>
                        <div>
                            <span class='category-name' id="${category}-label">${category}</span>
                        </div>
                        <ul>
                            ${category_dict[category].map(foodlist_template).join("")}
                        </ul>
                    </div>
                    `
                )
            }).join("")
        }`

        // add animation for scrolling component when a category in the category list is clicked
        // Will scroll to the section containing the food for that category
        for (let i=0; i < uniq_categories.length; i++){
            $("[id='"+uniq_categories[i]+"-scroll']").click(function() {
                $('html,body').animate({
                        scrollTop: $("[id='"+uniq_categories[i]+"-label']").offset().top},
                    'slow');
            });
        }
    }
});
