type Page = {
    link: string;
    title: string;
    icon?: string;
    shortDescription?: string;
    isStartPage?: boolean;
}

const pages: Page[] = [
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

export function getPages(): Readonly<Page[]> {
    return pages.map(p => ({...p}));
}
