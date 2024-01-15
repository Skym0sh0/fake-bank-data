const pages = [
    {
        link: "/", title: "Home",
        icon: 'mdi-apps',
        isStartPage: true,
    },
    // {
    //     link: "/statements",
    //     title: "Statement Overview",
    //     icon: 'mdi-cash',
    // },
    {
        link: "/turnovers",
        title: "Turnover Overview",
        icon: 'mdi-cash-multiple',
    },
    {
        link: "/categories",
        title: "Category Overview",
        icon: 'mdi-home-group',
    },
    {
        link: "/reports",
        title: "Reports",
        icon: 'mdi-chart-bar',
    },
    {
        link: "/about",
        title: "About",
        icon: 'mdi-help-circle',
    },
];

function getPages() {
    return pages.map(p => ({...p}))
}

export default getPages;
