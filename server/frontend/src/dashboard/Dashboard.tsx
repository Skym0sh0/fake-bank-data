import React, {useMemo} from "react";
import _ from "lodash";
import {getPages, Page} from "../router/Routes";
import {Card, CardActionArea, CardContent, CardHeader, Paper, Tooltip} from "@mui/material";
import './Dashboard.css';
import Typography from "@mui/material/Typography";

export default function Dashboard() {
    const items = useMemo(() => {
        return _.sortBy(getPages(), p => p.title);
    }, []);

    return <Paper className="DashboardTiles">
        {
            items.map(page => <DashboardTile key={page.path} page={page}/>)
        }
    </Paper>;
}

function DashboardTile({page}: { page: Page }) {
    return <Tooltip title={page.description ?? page.shortDescription ?? page.title}>
        <Card elevation={8} className="Tile">
            <CardActionArea href={page.path} className="TileArea">
                <CardHeader title={page.title}
                            subheader={page.shortDescription}>
                </CardHeader>

                <CardContent className="TileContent">
                    <Typography variant="caption">
                        {page.shortDescription}
                    </Typography>

                    {page.icon}
                </CardContent>
            </CardActionArea>
        </Card>
    </Tooltip>;
}
