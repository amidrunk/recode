module.exports = {
	entry: {
		"main": ['./src/main/javascript/main.jsx'],
		"vendor": ['script!jquery']
	},
	output: {
		filename: "./target/classes/[name].js"
	},
	module: {
    loaders: [
      {
        test: /.jsx?$/,
        loader: 'babel-loader',
        exclude: /node_modules/,
        query: {
          presets: ['es2015', 'react']
        }
      }
    ]
  }
};
