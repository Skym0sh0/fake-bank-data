import {
    Badge,
    Box,
    Container,
    CssBaseline,
    Divider,
    Grid,
    IconButton,
    List,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Paper,
    Toolbar,
    Typography
} from '@mui/material';
import React, {useEffect, useState} from 'react';
import {createTheme, styled, ThemeProvider} from '@mui/material/styles';
import MenuIcon from '@mui/icons-material/Menu';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import NotificationsIcon from '@mui/icons-material/Notifications';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import CardMembershipIcon from '@mui/icons-material/CardMembership';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import AppSettingsAltIcon from '@mui/icons-material/AppSettingsAlt';
import MuiDrawer from '@mui/material/Drawer';
import MuiAppBar, {AppBarProps as MuiAppBarProps} from '@mui/material/AppBar';

const drawerWidth: number = 240;

interface AppBarProps extends MuiAppBarProps {
    open?: boolean;
}

const AppBar = styled(MuiAppBar, {
    shouldForwardProp: (prop) => prop !== 'open',
})<AppBarProps>(({theme, open}) => ({
    zIndex: theme.zIndex.drawer + 1,
    transition: theme.transitions.create(['width', 'margin'], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    ...(open && {
        marginLeft: drawerWidth,
        width: `calc(100% - ${drawerWidth}px)`,
        transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
        }),
    }),
}));

const Drawer = styled(MuiDrawer, {shouldForwardProp: (prop) => prop !== 'open'})(
    ({theme, open}) => ({
        '& .MuiDrawer-paper': {
            position: 'relative',
            whiteSpace: 'nowrap',
            width: drawerWidth,
            transition: theme.transitions.create('width', {
                easing: theme.transitions.easing.sharp,
                duration: theme.transitions.duration.enteringScreen,
            }),
            boxSizing: 'border-box',
            ...(!open && {
                overflowX: 'hidden',
                transition: theme.transitions.create('width', {
                    easing: theme.transitions.easing.sharp,
                    duration: theme.transitions.duration.leavingScreen,
                }),
                width: theme.spacing(7),
                [theme.breakpoints.up('sm')]: {
                    width: theme.spacing(9),
                },
            }),
        },
    }),
);


const defaultTheme = createTheme();

function App() {
    const [open, setOpen] = React.useState(true);
    const toggleDrawer = () => {
        setOpen(!open);
    };

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

    return (
        <ThemeProvider theme={defaultTheme}>
            <Box sx={{display: 'flex'}}>
                <CssBaseline/>

                <AppBar position="absolute" open={open}>
                    <Toolbar
                        sx={{
                            pr: '24px', // keep right padding when drawer closed
                        }}
                    >
                        <IconButton
                            edge="start"
                            color="inherit"
                            aria-label="open drawer"
                            onClick={toggleDrawer}
                            sx={{
                                marginRight: '36px',
                                ...(open && {display: 'none'}),
                            }}
                        >
                            <MenuIcon/>
                        </IconButton>
                        <Typography
                            component="h1"
                            variant="h6"
                            color="inherit"
                            noWrap
                            sx={{flexGrow: 1}}
                        >
                            Dashboard
                        </Typography>
                        <IconButton color="inherit">
                            <Badge badgeContent={4} color="secondary">
                                <NotificationsIcon/>
                            </Badge>
                        </IconButton>
                    </Toolbar>
                </AppBar>
                <Drawer variant="permanent" open={open}>
                    <Toolbar
                        sx={{
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'flex-end',
                            px: [1],
                        }}
                    >
                        <IconButton onClick={toggleDrawer}>
                            <ChevronLeftIcon/>
                        </IconButton>
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
                <Box
                    component="main"
                    sx={{
                        backgroundColor: (theme) =>
                            theme.palette.mode === 'light'
                                ? theme.palette.grey[100]
                                : theme.palette.grey[900],
                        flexGrow: 1,
                        height: '100vh',
                        overflow: 'auto',
                    }}
                >
                    <Toolbar/>
                    <Container maxWidth="lg" sx={{mt: 4, mb: 4}}>
                        <Grid container spacing={3}>
                            <ShowFormats/>

                            <Grid item xs={12} md={8} lg={9}>
                                <Paper sx={{
                                    p: 2,
                                    display: 'flex',
                                    flexDirection: 'column',
                                    height: 240,
                                }}>
                                    <ShowFormats/>
                                </Paper>
                            </Grid>
                            <Grid item xs={12} md={4} lg={3}>
                                <Paper sx={{
                                    p: 2,
                                    display: 'flex',
                                    flexDirection: 'column',
                                    height: 240,
                                }}>
                                    <ShowFormats/>
                                </Paper>
                            </Grid>
                            <Grid item xs={12}>
                                <Paper sx={{p: 2, display: 'flex', flexDirection: 'column'}}>
                                    <ShowFormats/>
                                </Paper>
                            </Grid>
                        </Grid>
                    </Container>
                </Box>
            </Box>
        </ThemeProvider>
    );
}

function ShowFormats() {
    const [isLoading, setIsLoading] = useState(false);

    const [formats, setFormats] = useState<string[]>([]);

    useEffect(() => {
        setIsLoading(true)

        setTimeout(() => {
            fetch("/api/turnover-import/formats", {
                headers: [
                    ['Authorization', 'Basic ' + window.btoa("default_user" + ":" + "password1234")],
                ],
            })
                // .then(response => {
                //     console.log("Regular Response", response)
                //     return response;
                // })
                .then((response => response.json()))
                .then(json => json as string[])
                .then(frmts => setFormats(frmts))
                .catch(error => console.error("Error", error))
                .finally(() => setIsLoading(false))
        }, 1500)
    }, []);

    if (isLoading)
        return <div>Loading ...</div>

    if (!formats.length)
        return <div>no formats</div>;

    return <div>
        <h4>Formate</h4>
        <ul>
            {formats.map(f => {
                return <li key={f}>
                    {f}
                </li>
            })}
        </ul>
    </div>;
}

export default App;
