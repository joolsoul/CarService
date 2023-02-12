$('.single-item').slick({
    infinite: true,

    arrows: true,

    prevArrow: '<svg class="bi bi-arrow-left-circle slider-arrow-left" xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" ' +
        ' viewBox="0 0 16 16">\n' +
        '<path fill-rule="evenodd" d="M1 8a7 7 0 1 0 14 0A7 7 0 ' +
        '0 0 1 8zm15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-4.5-.5a.5.5 ' +
        '0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 ' +
        '0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z"/>\n' +
        '</svg>',
    nextArrow: '<svg class="bi bi-arrow-right-circle slider-arrow-right" xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" ' +
        'viewBox="0 0 16 16">\n' +
        '  <path fill-rule="evenodd" d="M1 8a7 7 0 1 0 14 0A7 ' +
        '7 0 0 0 1 8zm15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM4.5 7.5a.5.5 0 ' +
        '0 0 0 1h5.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3a.5.5 0 0 0 0-.708l-3-3a.5.5 0 ' +
        '1 0-.708.708L10.293 7.5H4.5z"/>\n' +
        '</svg>',

    slidesToShow: 3,
    slidesToScroll: 1
});