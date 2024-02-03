const pages = [
    {
        link: "/",
        title: "Dashboard",
        icon: 'mdi-view-dashboard',
        shortDescription: "Dashboard",
        isStartPage: true,
    },
    {
        link: "/turnovers",
        title: "Umsätze",
        shortDescription: "Umsätze eintragen",
        icon: 'mdi-cash-multiple',
    },
    {
        link: "/categories",
        title: "Kategorien",
        shortDescription: "Kategorien pflegen",
        icon: 'mdi-home-group',
    },
    {
        link: "/reports",
        title: "Berichte",
        shortDescription: "Einfache Berichte",
        icon: 'mdi-chart-bar',
    },
    {
        link: "/timely-reports",
        title: "Zeitreihen Berichte",
        shortDescription: "Berichte über Zeitreihen",
        icon: 'mdi-chart-areaspline',
    },
    {
        link: "/about",
        title: "Hilfe",
        shortDescription: "Hilfe",
        icon: 'mdi-help-circle',
    },
];

function getPages() {
    return pages.map(p => ({...p}))
}

export default getPages;
