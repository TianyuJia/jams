import config from "../../../config";

export default {
	data() {
		return {
			workspaces: []
		};
	},
	methods: {
		formatDate(value) {
			let date = new Date(value);
			return date.getFullYear() + "-" +
				((date.getMonth() + 1) < 10 ? "0" : "") + (date.getMonth() + 1) + "-" +
				(date.getDate() < 10 ? "0" : "") + date.getDate() + " ";
		}
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
