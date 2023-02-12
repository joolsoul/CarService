document.querySelectorAll('.plus').forEach(item => {

    item.addEventListener('click', function () {
        let id = item.id;
        id = id.substring(2);
        let inp = document.getElementById("input_" + id);
        if (parseInt(inp.value) < parseInt(inp.max)) {
            inp.value++;
        }

    });
});

document.querySelectorAll('.minus').forEach(item => {

    item.addEventListener('click', function () {
        let id = item.id;
        id = id.substring(2);
        let inp = document.getElementById("input_" + id);
        if (inp.value > 1) {
            inp.value--;
        }
    });
});




