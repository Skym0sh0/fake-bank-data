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
import {useAuth} from "../login/useAuth";

export default function AppLayout({children}: { children?: React.ReactNode }) {
    const theme = useTheme();

    const auth = useAuth();

    const logout = () => {
        auth.logout()
            .then(() => window.location.reload())
    };

    const [open, setOpen] = useState(true);
    const toggleOpen = () => setOpen(prev => !prev);

    console.log("refresh layout", auth)

    return (
        <Box className="AppLayout">
            {auth.user &&
                <Paper className="NavigationBarBox" sx={{backgroundColor: theme.palette.grey.A400}} elevation={8}>
                    <NavigationBar open={open} onClose={() => setOpen(false)}/>
                </Paper>
            }
            <Box className="MainAppView">
                <AppBar position="static">
                    <Toolbar>
                        {auth.user &&
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

                        {
                            <div>
                                {auth.user ? "Eingeloggt" : "Nicht eingeloggt"}
                            </div>
                        }

                        {!auth.user &&
                            <Button color="inherit" href="/login">
                                Login
                            </Button>
                        }

                        {auth.user &&
                            <Button color="inherit" onClick={logout}>
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
