import {Divider, Drawer, List, ListItemButton, ListItemIcon, ListItemText, Toolbar} from "@mui/material";
import React from "react";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import CardMembershipIcon from "@mui/icons-material/CardMembership";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import AppSettingsAltIcon from "@mui/icons-material/AppSettingsAlt";

export default function Sidebar() {
    const items = [
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
    ];

    return <Drawer variant="permanent" open={true}>
        <Toolbar
            sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-start',
            }}
        >
        </Toolbar>

        <Divider/>

        <List component="nav">
            {
                items.map(item => {
                    return <ListItemButton key={item.text}>
                        <ListItemIcon>
                            {item.icon}
                        </ListItemIcon>
                        <ListItemText primary={item.text}/>
                    </ListItemButton>
                })
            }
            <Divider sx={{my: 1}}/>
            {
                items.map(item => {
                    return <ListItemButton key={item.text}>
                        <ListItemIcon>
                            {item.icon}
                        </ListItemIcon>
                        <ListItemText primary={item.text}/>
                    </ListItemButton>
                })
            }

        </List>
    </Drawer>
}
