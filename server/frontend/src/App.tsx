import * as React from 'react';
import {useState} from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import CardMembershipIcon from "@mui/icons-material/CardMembership";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import AppSettingsAltIcon from "@mui/icons-material/AppSettingsAlt";
import {Divider, List, ListItemButton, ListItemIcon, ListItemText, Paper} from "@mui/material";

const defaultTheme = createTheme();

function NavigationBar() {
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

    return <>
        <Toolbar/>
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
    </>;
}

export default function App() {
    const [open, setOpen] = useState(true);
    const toggleOpen = () => setOpen(prev => !prev);

    return (
        <ThemeProvider theme={defaultTheme}>
            <Box sx={{display: 'flex', minHeight: "100vh", minWidth: "100vw"}}>
                <Paper sx={{height: "100vh"}} elevation={10}>
                    {open ? "Open":"Closed"}
                    <NavigationBar/>
                </Paper>
                <Box sx={{width: '100%', height: '100%'}}>
                    <AppBar position="static">
                        <Toolbar>
                            <IconButton size="large"
                                        edge="start"
                                        color="inherit"
                                        aria-label="menu"
                                        sx={{mr: 2}}
                                        onClick={toggleOpen}>
                                <MenuIcon/>
                            </IconButton>

                            <Typography variant="h6" component="div" sx={{flexGrow: 1}}>
                                Regular Income
                            </Typography>

                            <Button color="inherit">
                                Login
                            </Button>
                        </Toolbar>
                    </AppBar>
                </Box>
            </Box>
        </ThemeProvider>
    );
}
