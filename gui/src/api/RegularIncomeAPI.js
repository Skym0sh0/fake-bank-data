import axios from "axios";
import {v4 as uuidv4} from 'uuid';
import {
    denormalizeCategory,
    denormalizeTurnoverImport,
    denormalizeTurnoverPreview,
    denormalizeUser
} from "@/util/Normalizer";
import {userService} from '@/auth/auth-header';

function authHeader(user) {
    if (user && user.username && user.password) {
        const authdata = window.btoa(user.username + ":" + user.password)
        return {'Authorization': 'Basic ' + authdata};
    } else {
        return {};
    }
}

class RegularIncomeAPI {
    constructor(baseUrl) {
        if (!baseUrl)
            throw Error(`baseUrl '${baseUrl}' must be set`)

        this.baseUrl = baseUrl
    }

    getClient(auth = authHeader(userService.getUser())) {
        const client = axios.create({
            baseURL: this.baseUrl,
            // timeout: 1500,
            headers: {
                correlationid: uuidv4(),
                ...auth
            },
        })

        client.interceptors.response.use(
            response => {
                // console.log("success", response)
                return response;
            },
            error => {
                if (error.response.status === 401) {
                    userService.logout()
                    location.reload();
                }

                // console.log("failed", error.response)
                return error;
            }
        )

        return client;
    }

    getAuth() {
        const ref = this

        return {
            registerUser(user) {
                return ref.getClient({})
                    .post("user/register", user)
                    .then(res => denormalizeUser(res.data));
            },

            login(username, password) {
                return ref.getClient(authHeader({username: username, password: password}))
                    .get("auth/login")
                    .then(res => denormalizeUser(res.data));
            },

            updateUser(id, user) {
                return ref.getClient()
                    .patch(`user/${id}/details`, user)
                    .then(res => denormalizeUser(res.data));
            },

            deleteUser(id) {
                return ref.getClient()
                    .delete(`user/${id}`);
            },
        }
    }

    getFileImports() {
        const ref = this

        return {
            putFileToImport(file) {
                const formData = new FormData()
                formData.append('file', file)

                return ref.getClient().put(
                    'statements/import',
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }
                )
            },
        }
    }

    getReports() {
        const ref = this

        return {
            fetchStatementsReport(begin, end) {
                return ref.getClient().get('reports/statements', {
                    params: {
                        begin: begin,
                        end: end,
                    }
                }).then(res => res.data)
            },

            fetchIncomeExpenseReport() {
                return ref.getClient().get('reports/monthly-income-expenses')
                    .then(res => res.data)
            },

            fetchIncomeExpenseFlowReport() {
                return ref.getClient().get('reports/income-expenses-flow')
                    .then(res => res.data)
            },
        }
    }

    getTurnovers() {
        const ref = this

        return {
            getSupportedPreviewFormats() {
                return ref.getClient()
                    .get("turnover-import/formats")
                    .then(res => res.data)
            },

            rawCsvTablePreview(file, encoding) {
                const formData = new FormData()
                formData.append('file', file)

                return ref.getClient().post(
                    `turnover-import/preview/csv`,
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        },
                        params: {
                            encoding: encoding,
                        },
                    },
                ).then(res => res.data);
            },

            previewTurnoverImport(type, file, encoding) {
                const formData = new FormData()
                formData.append('file', file)

                return ref.getClient().post(
                    `turnover-import/preview`,
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        },
                        params: {
                            format: type,
                            encoding: encoding
                        },
                    },
                ).then(res => denormalizeTurnoverPreview(res.data));
            },

            createTurnoverImport(file, format, encoding, data) {
                const formData = new FormData()
                formData.append('file', file)
                formData.append('data', new Blob([JSON.stringify({
                    format: format,
                    encoding: encoding,
                    rows: data,
                })], {type: "application/json"}))

                return ref.getClient().post(
                    'turnover-import',
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    },
                ).then(res => denormalizeTurnoverImport(res.data));
            },

            fetchTurnoverImports() {
                return ref.getClient().get('turnover-import')
                    .then(res => res.data.map(c => denormalizeTurnoverImport(c)))
            },

            fetchTurnoverImport(id) {
                return ref.getClient().get(`turnover-import/${id}`)
                    .then(res => denormalizeTurnoverImport(res.data))
            },

            patchTurnovers(id, changes) {
                return ref.getClient().patch(`turnover-import/${id}`, changes)
                    .then(res => denormalizeTurnoverImport(res.data))
            },

            deleteTurnoverImport(turnoverImport) {
                return ref.getClient().delete(`turnover-import/${turnoverImport.id}`)
            },
        };
    }

    getCategories() {
        const ref = this

        return {
            // fetchCategories() {
            //     return ref.getClient().get('categories')
            //         .then(res => res.data.map(c => denormalizeCategory(c)))
            // },

            fetchCategoryTree() {
                return ref.getClient().get('categories/tree')
                    .then(res => res.data.map(c => denormalizeCategory(c)))
            },

            postCategory(category) {
                return ref.getClient().post('categories', category)
                    .then(res => denormalizeCategory(res.data))
            },

            postChildCategory(parentId, category) {
                return ref.getClient().post(`categories/${parentId}/children`, category)
                    .then(res => denormalizeCategory(res.data))
            },

            patchCategory(category) {
                return ref.getClient().patch(`categories/${category.id}`, category)
                    .then(res => denormalizeCategory(res.data))
            },

            deleteCategory(category) {
                return ref.getClient().delete(`categories/${category.id}`)
            },

            reassignCategory(newParent, child) {
                return ref.getClient().patch(`categories/${newParent.id}/children/${child.id}`)
                    .then(res => denormalizeCategory(res.data))
            }
        }
    }
}

const api = new RegularIncomeAPI(process.env.VUE_APP_CONFIG_BACKEND_URL)

export {
    RegularIncomeAPI,
    api
}
