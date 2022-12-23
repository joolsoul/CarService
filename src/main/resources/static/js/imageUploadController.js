let inp = document.getElementById("imagesToDelete");

document.querySelectorAll('.deleteButton').forEach(item => {
    item.addEventListener('click', function () {
        let id = item.id;
        id = id.substring(4);

        let img = document.getElementById("img_" + id);
        img.hidden = true;
        item.hidden = true;

        let imgPath = document.getElementById("path_" + id).textContent;

        if (inp.value != "") {
            inp.value += ";" + imgPath;
        } else {
            inp.value = imgPath;
        }

    });
});