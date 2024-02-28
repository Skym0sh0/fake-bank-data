import {useMemo} from "react";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import CardMembershipIcon from "@mui/icons-material/CardMembership";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import AppSettingsAltIcon from "@mui/icons-material/AppSettingsAlt";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import {Divider, List, ListItemButton, ListItemIcon, ListItemText} from "@mui/material";
import * as React from "react";

type NavigationBarProps = {
    open: boolean;
    onClose: () => void;
}

export default function NavigationBar({open, onClose}: NavigationBarProps) {
    const items = useMemo(() => [
        {
            text: "ToDo",
            icon: <ErrorOutlineIcon color="error"/>,
        },
        {
            text: "Shopping",
            icon: <ShoppingCartIcon/>,
        },
        {
            text: "Membership",
            icon: <CardMembershipIcon/>,
        },
        {
            text: "Shipping",
            icon: <LocalShippingIcon/>,
        },
        {
            text: "Settings",
            icon: <AppSettingsAltIcon/>,
        }
    ], []);

    return <>
        <Toolbar sx={{display: 'flex', alignItems: 'center', justifyContent: 'flex-end',}}>
            {open && <IconButton onClick={onClose}>
                <ChevronLeftIcon/>
            </IconButton>}
        </Toolbar>

        <Divider/>

        <List component="nav">
            {
                items.map(item => {
                    return <ListItemButton key={item.text}>
                        <ListItemIcon sx={{minWidth: open ? null : 0}}>
                            {item.icon}
                        </ListItemIcon>
                        {open && <ListItemText primary={item.text}/>}
                    </ListItemButton>
                })
            }
        </List>
    </>;
}
