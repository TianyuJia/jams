import config from "../../../config";
import * as flashes from "../../../flashes";
import {formatDateTime} from "../../../date";

const flashIdLoadingWorkspacesFailed = 1;
const flashIdRemovingWorkspaceFailed = 2;

export default {
	beforeDestroy() {
		clearInterval(this.workspacesIntervalId);
		window.removeEventListener("online", this.getWorkspaces);
	},
	created() {
		this.getWorkspaces(true);
		this.workspacesIntervalId = setInterval(this.getWorkspaces, config.workspacesInterval);
		window.addEventListener("online", this.getWorkspaces);
	},
	data() {
		return {
			workspaces: [],
			workspacesIntervalId: 0
		};
	},
	methods: {
		formatDateTime,

		getDownloadUrl(workspaceId) {
			return config.apiBaseUrl + "/workspace/download/" + workspaceId;
		},

		getWorkspaces(force = false) {
			if (!this.$store.state.isOnline) {
				return;
			}

			if (!force && !this.$store.state.isConnected) {
				return;
			}

			flashes.clear(flashIdLoadingWorkspacesFailed);

			const url = config.apiBaseUrl + "/workspace/find";

			this.$http.get(url).then((response) => {
				response.json().then((data) => {
					this.workspaces = data.workspaces;
				}, (response) => {
					console.error("workspaces: Parsing JSON response failed:", response);
				});
			}, (response) => {
				flashes.error("Workspace list couldn’t be loaded", flashIdLoadingWorkspacesFailed);
			});
		},

		removeWorkspace(workspace) {
			flashes.clear(flashIdRemovingWorkspaceFailed);

			const message = "Remove workspace “" + workspace.name + "”?";

			if (!window.confirm(message)) {
				return;
			}

			const url = config.apiBaseUrl + "/workspace/" + workspace.id + "/delete";

			this.$http.get(url).then((response) => {
				response.json().then((data) => {
					for (let i = 0; i < this.workspaces.length; i++) {
						if (this.workspaces[i].id === data.id) {
							this.workspaces.splice(i, 1);
							break;
						}
					}
				}, (response) => {
					console.error("workspaces: Parsing JSON response failed:", response);
				});
			}, (response) => {
				flashes.error("Workspace “" + workspace.name + "” couldn’t be removed", flashIdRemovingWorkspaceFailed);
			});
		}
	}
};
