import * as React from "react";
import {useMemo} from "react";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import {Divider, List, ListItemButton, ListItemIcon, ListItemText} from "@mui/material";
import Typography from "@mui/material/Typography";
import {getPagesToDisplay} from "../router/Routes";
import _ from "lodash";

type NavigationBarProps = {
    open: boolean;
    onClose: () => void;
}

export default function NavigationBar({open, onClose}: NavigationBarProps) {
    const items = useMemo(() => {
        return _.sortBy(getPagesToDisplay(), p => p.title);
    }, []);

    return <>
        <NavigationToolbar open={open} onClose={onClose}/>

        <Divider/>

        <List component="nav">
            {
                items.map(item => {
                    return <ListItemButton key={item.path} href={item.path} component="a">
                        {item.icon &&
                            <ListItemIcon sx={{minWidth: open ? null : 0}}>
                                {item.icon}
                            </ListItemIcon>
                        }

                        {open && <ListItemText primary={item.title}/>}
                    </ListItemButton>
                })
            }
        </List>

        <Divider/>
    </>;
}

function NavigationToolbar({open, onClose}: NavigationBarProps) {
    if (!open)
        return <Toolbar/>

    return <Toolbar sx={{display: 'flex', alignItems: 'center', justifyContent: 'space-between',}}>
        <div>
            <Typography variant="caption">
                Inhalte
            </Typography>
        </div>

        <IconButton onClick={onClose}>
            <ChevronLeftIcon/>
        </IconButton>
    </Toolbar>
}
