import config from "../../../config";
import {formatDateTime} from "../../../date";

export default {
	data() {
		return {
			workspaces: []
		};
	},
	methods: {
		formatDateTime
	},
	mounted() {
		const url = config.baseUrl + "/workspace/find";

		this.$http.get(url).then((response) => {
			response.json().then((data) => {
				console.debug(response.data);
				this.workspaces = data.workspaces;
			}, (response) => {
				console.error("Jobs: Parsing JSON response failed:", response);
			});
		}, (response) => {
			console.error("Jobs: Unexpected response:", response);
		});
	}
};