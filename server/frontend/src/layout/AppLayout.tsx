import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import {Paper, useTheme} from "@mui/material";
import * as React from "react";
import {useState} from "react";
import Box from "@mui/material/Box";
import AppBar from "@mui/material/AppBar";
import MenuIcon from "@mui/icons-material/Menu";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import NavigationBar from "./NavigationBar";
import './AppLayout.css';

export default function AppLayout({children}: { children?: React.ReactNode }) {
    const theme = useTheme();

    const [isLoggedIn, setIsLoggedIn] = useState(false);

    const [open, setOpen] = useState(true);
    const toggleOpen = () => setOpen(prev => !prev);

    return (
        <Box className="AppLayout">
            {isLoggedIn &&
                <Paper className="NavigationBarBox" sx={{backgroundColor: theme.palette.grey.A400}} elevation={8}>
                    <NavigationBar open={open} onClose={() => setOpen(false)}/>
                </Paper>
            }
            <Box className="MainAppView">
                <AppBar position="static">
                    <Toolbar>
                        {isLoggedIn &&
                            <IconButton size="large"
                                        edge="start"
                                        color="inherit"
                                        sx={{mr: 2}}
                                        onClick={toggleOpen}>
                                <MenuIcon/>
                            </IconButton>
                        }

                        <Typography variant="h6" component="div" sx={{flexGrow: 1}}>
                            Regular Income
                        </Typography>

                        {!isLoggedIn &&
                            <Button color="inherit" onClick={() => setIsLoggedIn(true)}>
                                Login
                            </Button>
                        }
                        {isLoggedIn &&
                            <Button color="inherit" onClick={() => setIsLoggedIn(false)}>
                                Logout
                            </Button>
                        }
                    </Toolbar>
                </AppBar>

                <Box className="MainAppContent">
                    {children}
                </Box>
            </Box>
        </Box>
    );
}
