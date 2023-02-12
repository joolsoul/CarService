let selForm = document.getElementById("sortSelectForm");


document.querySelectorAll('.sorting').forEach(item => {

    item.addEventListener('change', function () {
        selForm.submit();
    });
});